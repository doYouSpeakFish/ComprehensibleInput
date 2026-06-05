# Text Adventure rebuild — plan

Rebuild the in-app text adventure feature from scratch (the existing app code is a
prototype and is deleted), add a home screen, and integrate with the authenticated
**v1** text-adventure backend endpoints using an offline-first approach. The backend
and the shared `:textadventuremodels` module are **not** changed.

The work is broken into vertical slices (increments). Each increment is end-to-end
verifiable via Cucumber + Roborazzi. Scenarios are specified up front in the feature
files, tagged `@incrementN`, and tagged `@ignore` until that increment is implemented
(removing `@ignore` is part of implementing the increment). `@ignore`/`@Ignore`
scenarios are excluded from discovery by `CucumberComposeScenarios` (see that file).

## Key decisions

- **Modules** (mirroring the existing `:data:account` / `:feature:account` split):
  - `:feature:home` — home screen only (no data module at this stage).
  - `:data:textadventure` — offline-first repository, Room storage, v1 remote source.
  - `:feature:textadventure` — list + chat screens, view models, use cases, navigation.
  - The `User` entity lives in `:common` (so it can be the FK target for adventures);
    upserting the user is the responsibility of `:data:account`.
- **User id** comes from the backend: after sign-in the account module calls
  `GET /v1/me` (returns `{ id, email }`) and upserts a local `User(id, email)`. This
  id equals the backend `account_id`, so it scopes locally-cached adventures correctly.
  No backend change is required.
- **Feature flag**: reuse the existing `aiTextAdventuresEnabled` flag (and the existing
  `@aiTextAdventuresDisabled` Cucumber tag). The flag hides the Text Adventures option
  on the home screen.
- **Repository boundaries**: `:data:textadventure` must **not** depend on
  `:data:account`. The session token and user id are passed in as arguments. The
  feature layer composes them, e.g. a use case flat-maps the user flow onto the
  adventures flow; view models observe the session-token flow to hold the current
  token for backend calls.
- **Offline-first**: the local Room database is the source of truth the UI observes.
  Network calls (refresh / start / send / generate) upsert into the DB, which the UI
  flow re-emits. Reads are scoped by `userId`; network calls take the session token.
- **v1 DTOs**: `:textadventuremodels` already holds the message/paragraph/start DTOs
  and is reused. The two v1 shapes that currently live only in the backend (the list
  `{items:[{id,title,learningLanguage,translationLanguage,updatedAt}]}` response and
  the v1 request bodies — note v1 uses `translationLanguage`, singular) are duplicated
  on the client inside `:data:textadventure` to avoid touching the backend.
- **Home screen**: two cards — "Stories" (opens the existing story list) and
  "Text Adventures" — with a Settings action in the top app bar. Home becomes the
  start destination.
- **Signed-out text adventures**: the screen shows an in-screen prompt with a button
  that opens the account screen.
- **Adventure deletion**: included in the list screen increment, wired to
  `DELETE /v1/adventures/{id}`, with optimistic removal + restore on failure.

## Backend v1 contract (reference, unchanged)

Authenticated with `Authorization: Bearer {token}` (`POST /v1/auth/sessions` →
`{access_token, token_type}`; the local user id comes from `GET /v1/me`).

- `POST /v1/adventures` `{learningLanguage, translationLanguage}` → 201
  `TextAdventureRemoteResponse` (first AI message).
- `GET /v1/adventures` → `{items:[{id,title,learningLanguage,translationLanguage,updatedAt}]}`.
- `GET /v1/adventures/{id}` → summary; `GET /v1/adventures/{id}/messages` →
  `TextAdventureMessagesRemoteResponse` (full message tree).
- `POST /v1/adventures/{id}/messages` `{type:"user"|"AI", parentId, text?}` → 201
  created message (user input is structured + translated; AI reply generated
  synchronously). 400/404/500 on bad input / not owned / generation failure.
- `DELETE /v1/adventures/{id}` and `DELETE /v1/adventures`.

## Target module graph

```
:common            UserEntity (@Entity), theme, DefaultPreview, DI utils   (+room-runtime)
:data:account      AccountRepository, UserLocalDataSource (@Dao upsert)    (+room-runtime, :common)
:data:textadventure  TextAdventureRepository, Room entities/DAO (FK->User), v1 remote
                                                                           (:common, :textadventuremodels)
:feature:home      HomeScreen + previews + testFixtures robot              (:common)
:feature:textadventure  list + chat screens, view models, use cases, nav
                                                                           (:common, :data:textadventure, :data:account)
:app               AppDb registers User + adventure entities/DAOs; NavHost wires home/stories/
                   text adventures/settings; DI injects new data sources   (depends on all)
```

`AppDb` (in `:app`) is the single Room database. Room's KSP runs in `:app`, which sees
`UserEntity` (`:common`), `UserLocalDataSource` (`:data:account`) and the adventure
entities/DAO (`:data:textadventure`); the `Adventure -> User` foreign key works because
both entities live in the same database.

## Data-layer API sketch (`:data:textadventure`)

```
interface TextAdventureRepository {
    // offline reads, scoped by the local user id (FK)
    fun getAdventures(userId: String): Flow<List<AdventureSummary>>
    fun getMessages(adventureId: String): Flow<List<AdventureMessage>>

    // network calls take the session token; they upsert into the DB
    suspend fun refreshAdventures(session: Session): Result<Unit>
    suspend fun refreshMessages(session: Session, adventureId: String): Result<Unit>
    suspend fun startAdventure(session: Session, learningLanguage: String,
                               translationLanguage: String): Result<String /* adventureId */>
    suspend fun sendUserMessage(session: Session, adventureId: String,
                                parentId: String, text: String): Result<AdventureMessage>
    suspend fun generateAiMessage(session: Session, adventureId: String,
                                  parentId: String): Result<AdventureMessage>
    suspend fun deleteAdventure(session: Session, adventureId: String): Result<Unit>
}
```

Feature layer (illustrative):

```
// use case: flat-map the user flow onto the adventures flow (no repo->repo dependency)
operator fun invoke() = accountRepository.user.flatMapLatest { user ->
    user?.let { textAdventureRepository.getAdventures(userId = it.id) } ?: flowOf(emptyList())
}

// view model: hold the current session token from the session flow
private var session: Session? = null
init { viewModelScope.launch { sessionUseCase().collect { session = it } } }
```

## Increments

### Increment 1 — Home screen, navigation, prototype removal, flag gating
- Delete the app text-adventure prototype: `data/textadventures/**`, `ui/textadventure/**`,
  the four use cases, the three `TextAdventureMigration*` files, prototype tests/fixtures,
  and the related kover coverage snapshots; remove text-adventure registrations from
  `AppDb`, `DataSourcesModule`, the kover excludes and the `textadventuremodels`
  dependency in `:app`; remove text-adventure items from the story list
  (`StoryList*`), `ComprehensibleInputApp` and `text_adventure.feature` /
  `story_list.feature`'s `@aiTextAdventuresDisabled` scenario; bump `AppDb` and add a
  migration dropping the prototype tables; update `AppDbMigrationTest`.
- Create `:feature:home` with `HomeScreen` (two cards + settings action), pure
  composable + public `@DefaultPreview`s (enabled + flag-disabled states), and a
  `HomeRobot` in `testFixtures`.
- Make home the start destination; wire `Home -> Stories` (existing list),
  `Home -> Settings`, and a placeholder `Home -> Text Adventures` (real screen lands in
  increment 3). Pass `textAdventuresEnabled` into `HomeScreen` from the app's
  `FeatureFlags`.
- Feature file: `app/.../features/home.feature` (`@increment1`, plus the
  `@increment3` text-adventures navigation scenario). Keep existing story scenarios
  green by routing "the story list is open" through home.
- **Verify**: app launches to home; navigate to stories/settings; Text Adventures card
  hidden under `@aiTextAdventuresDisabled`; previews screenshot cleanly.

### Increment 2 — User entity + account user data source + reactive flows
- Add `UserEntity(id, email)` to `:common`; add `UserLocalDataSource` (@Dao: upsert /
  get / observe / delete) to `:data:account` (+ Room deps); register both in `AppDb`
  and DI; migration adds the `user` table.
- Extend the account remote source with `GET /v1/me`. On successful sign-in: fetch
  `/v1/me`, upsert `User(id, email)`, store the user id in the DataStore session.
- Expose `AccountRepository.user: Flow<User?>` and the session/token flow; delete the
  local user on account deletion (drives the adventures cascade later).
- Feature file: `feature/account/.../features/account_user.feature` (`@increment2`).
  Account-module tests use an in-memory Room DB for the user DAO; extend `signInAs`
  to set a user id.
- **Verify**: signing in creates + exposes the user; sign-out keeps it; account
  deletion removes it.

### Increment 3 — Text adventures list (offline-first) + deletion
- Create `:data:textadventure`: Room entities (Adventure / Message / Sentence) with
  `Adventure.userId` FK to `UserEntity` (cascade), DAO, v1 remote source
  (`GET/DELETE /v1/adventures`), repository (`getAdventures`, `refreshAdventures`,
  `deleteAdventure`); register entities/DAO in `AppDb`; migration adds the tables.
- Create `:feature:textadventure`: list screen + view model + use case (flat-map user
  -> adventures), signed-out prompt, empty/loading/error states, delete (optimistic +
  restore on failure), and a "new adventure" entry point. Public `@DefaultPreview`s for
  every state; `TextAdventuresListRobot` in `testFixtures`. Wire `Home -> Text
  Adventures` to the real screen and `Account` navigation for the sign-in prompt.
- Feature file: `feature/textadventure/.../features/text_adventures_list.feature`
  (`@increment3`) and the `@increment3` home navigation scenario.
- **Verify**: signed-out prompt; signed-in list from cache + refresh; only own
  adventures; loading/error/empty; delete + restore-on-failure; previews screenshot
  cleanly.

### Increment 4 — Start an adventure + chat screen (first AI message)
- `:data:textadventure`: `startAdventure` (POST), `getMessages` (offline flow),
  `refreshMessages` (GET messages) + persistence/migrations for the message tree.
- `:feature:textadventure`: chat screen + view model. Starting navigates straight to
  the chat in a loading state; show a placeholder message that cycles phrases
  (e.g. "Plotting…"); on success show the first AI message; on failure show an error +
  retry (retry hides the error, shows the placeholder, re-requests). AI sentences are
  tap-to-translate (reuse `TranslatableText` / `StoryContentPart`, matching the story
  reader); scroll to new messages. Opening a cached adventure shows its messages
  immediately while refreshing. Public `@DefaultPreview`s for loading / loaded / error /
  translated states; `TextAdventureChatRobot` in `testFixtures`.
- Feature file: `text_adventure_chat.feature` `@increment4` scenarios.
- **Verify**: start → loading → first message; generation error → retry; tap-to-translate
  both directions; cached-first open; previews screenshot cleanly.

### Increment 5 — Continue the adventure (user messages + AI responses)
- `:data:textadventure`: `sendUserMessage` (POST user) and `generateAiMessage`
  (POST AI), persisting structured/translated content.
- `:feature:textadventure`: input bar; user message shown optimistically; becomes
  tap-to-translate once the submit succeeds; submit failure shows error + retry; then
  AI reply generates with the cycling placeholder, success shows the reply, failure
  shows error + retry; scroll to each new message; hide the input when an adventure
  ends. Extend previews for user-message / sending / ended states.
- Feature file: `text_adventure_chat.feature` `@increment5` scenarios.
- **Verify**: send shows immediately; translatable after submit; submit + AI errors with
  retry; AI placeholder → reply; scroll; input hidden on ending; previews screenshot
  cleanly.

## Cross-cutting conventions

- **Cucumber**: declarative Gherkin; step defs delegate to `testFixtures` robots; each
  feature module hosts its own `ParameterizedRobolectricTestRunner` + scenario holder
  (mirror `:feature:account`); fakes for remote sources, in-memory Room for local.
- **Screenshots**: every screen state has a public `@DefaultPreview`; add each new UI
  package to the module's `generateComposePreviewRobolectricTests { packages }`; record
  with `:module:recordRoborazzi` and review the PNGs under `<module>/screenshots/`.
- **Migrations**: each schema change bumps `AppDb` with a migration and updates
  `AppDbMigrationTest`.
- **Feature flag**: `aiTextAdventuresEnabled` gates the home Text Adventures card;
  `@aiTextAdventuresDisabled` drives the "hidden when disabled" test.

## Assumptions / notes for review

- Home screen layout, settings placement, signed-out prompt and in-list deletion follow
  the answers given when this plan was prepared.
- This document + the feature files are the planning deliverable; implementation lands
  increment by increment (each removes `@ignore` from its scenarios).
