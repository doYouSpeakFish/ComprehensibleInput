package input.comprehensible.data.stories.sources.stories.local

import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryElement
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Default implementation of [StoriesLocalDataSource] that provides the story content.
 */
class DefaultStoriesLocalDataSource(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : StoriesLocalDataSource {
    override suspend fun getStory(id: String) = withContext(dispatcher) {
        stories.firstOrNull { it.id == id }
    }

    override suspend fun getStories() = withContext(dispatcher) {
        StoriesList(
            stories = stories.map {
                StoriesList.StoriesItem(
                    id = it.id,
                    title = it.title,
                )
            }
        )
    }
}

private val stories = listOf(
    Story(
        id = "1",
        title = "Geheimes Heilbad [Secret Spa] - A2",
        content = listOf(
            StoryElement.Paragraph("Maria lebt in einem kleinen Dorf. Jeden Morgen geht sie spazieren im Wald neben ihrem Haus. Sie liebt die Ruhe und die frische Luft."),
            StoryElement.Paragraph("Eines Tages hört sie das Geräusch von fließendem [flowing] Wasser. Sie folgt dem Geräusch und entdeckt eine Quelle [Spring]. Das Wasser ist warm."),
            StoryElement.Paragraph("„Ein Heilbad!“, denkt sie. Ein Heilbad ist ein natürliches Wasserbecken mit warmem Wasser. Viele Menschen glauben, dass solches Wasser heilende Kräfte [healing powers] hat."),
            StoryElement.Paragraph("Maria hat eine Idee. Sie könnte hierher kommen und sich entspannen [relax]. Vielleicht könnte sie sogar ihre Freunde einladen [invite]."),
            StoryElement.Paragraph("Aber dann sieht sie etwas am Boden des Heilbades. Es ist ein kleiner, glänzender Gegenstand [shiny object]. Maria nimmt es hoch. Es ist eine alte Münze [coin]."),
            StoryElement.Paragraph("Plötzlich [suddenly] hört sie eine Stimme [voice] hinter sich: „Das ist meine Münze!“ Maria dreht sich um [Maria turned around] und sieht eine alte Frau. Die Frau sieht freundlich aus, aber ihre Augen sind traurig [sad]."),
            StoryElement.Paragraph("„Dieses Heilbad gehört [belongs to] meiner Familie“, sagt die alte Frau. „Ich habe diese Münze vor vielen Jahren hier verloren [lost here long ago].“"),
            StoryElement.Paragraph("Maria fühlt sich schlecht. Sie gibt der Frau die Münze zurück und entschuldigt sich."),
            StoryElement.Paragraph("Die alte Frau lächelt [smiled]. „Es ist in Ordnung“, sagt sie. „Aber dieses Heilbad hat ein Geheimnis [secret]. Es kann nur von denjenigen [those] gesehen werden, die reinen Herzens [pure of heart] sind.“"),
            StoryElement.Paragraph("Maria ist überrascht [surprised]. Sie dankt der Frau und verlässt [left] das Heilbad."),
            StoryElement.Paragraph("Am nächsten Tag bringt Maria ihre Freunde mit in den Wald. Aber sie können das Heilbad nicht finden. Es ist verschwunden [disappeared]."),
            StoryElement.Paragraph("Maria lächelt. Sie weiß jetzt, dass einige Dinge im Leben nur für sie bestimmt sind [She now knows that some things in life are meant only for her]."),
            StoryElement.Paragraph("Ende."),
        )
    ),
    Story(
        id = "2",
        title = "Kein Halt am Mars [No stop at Mars] - A2",
        content = listOf(
            StoryElement.Paragraph("Heute ist ein besonderer Tag. Ich bin der erste Mensch, der auf Phobos steht, einem kleinen Mond [moon] von Mars. Ich stehe hier in meinem Raumanzug [spacesuit] und schaue hoch zum roten Planeten. Mars ist so groß und wunderschön. Die Farben sind intensiv – rotes und oranges Licht. Es ist ein unglaublicher [incredible] Anblick."),
            StoryElement.Paragraph("Aber in meinem Herzen [heart] ist auch ein kleines bisschen Traurigkeit [sadness]. Ich werde nie auf Mars selbst laufen können. Diese Mission ist nur für Phobos. Aber ich bin auch aufgeregt [excited]. Phobos ist ein mysteriöser [mysterious] Ort, und ich bin der Erste, der ihn erkunden [explore] darf."),
            StoryElement.Paragraph("Die Schwerkraft [gravity] hier ist sehr niedrig. Ich fühle mich leicht und kann hoch springen. Jeder Schritt fühlt sich seltsam [strange] an, fast wie fliegen [flying]. Es ist lustig [funny] und macht mich glücklich."),
            StoryElement.Paragraph("Wir haben nur zwanzig Tage auf Phobos. Die Wissenschaftler [scientists] und Ingenieure [engineers] haben hart gearbeitet, um die Reise kurz zu machen. Es ist weniger gefährlich [dangerous] so. Ich möchte viel entdecken [discover] in dieser kurzen Zeit."),
            StoryElement.Paragraph("Ich freue mich auch darauf, nach Hause zu gehen. Ich vermisse die Wolken und den Regen. Ich stelle mir vor, wie ich wieder unter einem blauen Himmel stehe und das Wasser auf meiner Haut [skin] spüre [feel]."),
            StoryElement.Paragraph("Aber jetzt, hier auf Phobos, ist es Zeit für Abenteuer [adventures]. Es gibt so viel zu tun. Ich nehme meinen ersten Schritt und beginne mit meiner Arbeit. Es ist ein großer Moment – für mich und für alle Menschen auf der Erde."),
            StoryElement.Paragraph("Ende"),
        )
    ),
    Story(
        id = "3",
        title = "Der leere Karneval [The empty carnival] - A2",
        content = listOf(
            StoryElement.Paragraph("Es war einmal ein kleiner Ort in Deutschland, der für seinen bunten und lebhaften [vivid] Karneval bekannt [known] war. Jedes Jahr kamen Menschen von überall her, um die festlichen [festive] Aktivitäten und die fröhliche Musik zu genießen."),
            StoryElement.Paragraph("Aber dieses Jahr war etwas anders. Als Anna, eine junge Frau, die in der Nähe wohnte, zum Festplatz [fairground] kam, war alles merkwürdig still [strangely silent]. \"Wo sind alle Menschen?\" fragte sie sich. Die Straßen waren leer, keine Musik spielte, keine Kinder lachten."),
            StoryElement.Paragraph("Anna ging durch die Gassen [alleys] und sah die geschmückten [decorated] Buden [stalls], aber es gab keine Verkäufer, keine Glitzerlichter [sparkling lights], keine Ballons. Es war, als hätte der Karneval nie begonnen."),
            StoryElement.Paragraph("Plötzlich hörte sie ein leises [quiet] Geräusch [sound]. \"Hallo?\" rief sie. Keine Antwort. Sie folgte dem Geräusch und fand einen kleinen, verängstigten [frightened] Hund, der sich unter einer Bank [bench] versteckte [hiding]."),
            StoryElement.Paragraph("\"Was machst du denn hier ganz alleine?\" fragte Anna den Hund. Der Hund schaute sie mit großen Augen an, als wollte er sagen: \"Ich weiß es auch nicht.\""),
            StoryElement.Paragraph("Anna beschloss [decided], den Hund zu begleiten [accompany], und gemeinsam suchten sie nach Hinweisen [notes]. Sie fanden ein Plakat [poster], auf dem stand [that said]: \"Karneval verschoben [postponed]! Neue Termine folgen bald. [new dates coming soon]\""),
            StoryElement.Paragraph("Anna verstand jetzt, warum niemand da war. Der Karneval wurde verschoben [postponed], aber sie hatte nichts davon gewusst. Sie lächelte den Hund an und sagte: \"Nun, es sieht so aus, als hätten wir den ganzen Platz für uns.\""),
            StoryElement.Paragraph("Und so verbrachten Anna und der kleine Hund den Tag zusammen [an so Anna and the little dog spent the day together] auf dem leeren Festplatz, spielten zwischen den bunten Buden [colorful stalls] und genossen die unerwartete Ruhe [and enjoyed the unexpeted peace]."),
            StoryElement.Paragraph("Als es dunkel [dark] wurde, brachte Anna den Hund nach Hause und versprach [promised], ihn am nächsten Tag wiederzubesuchen [to visit again]. Vielleicht würde der Karneval bald für alle geöffnet werden, aber für heute war es ihr kleines Geheimnis [secret] - das Geheimnis [secret] des leeren Karnevals."),
            StoryElement.Paragraph("Und die Moral von der Geschichte? Manchmal, wenn die Dinge nicht so laufen, wie wir es erwarten [when things don't go as we expected], finden wir Freude in den ungewöhnlichsten Momenten [unusual moments] und Freundschaften an den unerwarteten Orten [unexpected places]."),
        )
    ),
    Story(
        id = "4",
        title = "Die winzigen Wikinger von Berlin [The tiny Vikings of Berlin] - A2",
        content = listOf(
            StoryElement.Paragraph("Es war einmal ein kleines Dorf [village] von Wikingern [vikings], nicht größer als Spielzeugfiguren [toy figures], die durch die Zeit segelten [sailed] und plötzlich im Herzen [heart] von Berlin landeten. Stellt [vorstellen=imagine] euch ihre Überraschung [surprise] vor, als sie ihre hölzernen Schiffe [wooden ships] am Ufer [shore] der Spree [river in Berlin] festmachten und von riesigen Gebäuden und leuchtenden Kutschen [glowing carriages] - oder wie wir sie nennen, Autos - umgeben [surrounded] waren."),
            StoryElement.Paragraph("Am Anfang [at the start] waren die kleinen Wikinger sehr verwirrt [confused]. \"Was ist das für ein Zauber [magic]?\" fragte der Anführer [leader], während er auf einen blinkenden Ampelmann zeigte [pointed at a flashing traffic light man]. \"Und warum bewegt [moves] sich diese Mauer [wall] dort von selbst?\" fügte er hinzu [he added], als eine U-Bahn [subway] an ihnen vorbeirauschte [rushed past]."),
            StoryElement.Paragraph("Aber Wikinger sind bekannt für ihre Anpassungsfähigkeit [adaptability] und ihren Entdeckergeist [spirit of discovery]. Sie beschlossen, diese neue Welt zu erkunden [explore]. Sie kletterten [climbed] auf Fahrräder, die ihnen wie riesige [giant] Schlachtschiffe [battle ships] vorkamen, und fuhren los [set off], um mehr zu entdecken [discover]."),
            StoryElement.Paragraph("Ihr erster Halt war der berühmte [famous] Alexanderplatz, wo sie sich unter die Menschenmengen [crowds] mischten. Die Leute bemerkten sie kaum [barely noticed them], abgelenkt [distracted] von ihren Handys [cell phones] und dem Trubel [hustle and bustle] der Stadt. Die Wikinger waren fasziniert von den vielen Ständen [stalls] mit Essen. \"Was für ein Festmahl [feast]!\", riefen sie und probierten ein Stück Döner, das fast so groß war wie sie selbst."),
            StoryElement.Paragraph("Nachdem sie sich gestärkt hatten [after they had regained their strength], setzten die Wikinger ihre Reise fort [fortsetzen=resume]. Sie kamen an einem Ort vorbei, der voller Schätze [treasures] zu sein schien [appeared to be] – ein Elektronikmarkt. Sie staunten [marveled] über die schimmernden Bildschirme [shimmering screens] und seltsamen Geräte [strange devices], die sie für Zaubergegenstände [magical artifacts] hielten [mistook]."),
            StoryElement.Paragraph("Am Ende des Tages waren die winzigen Wikinger erschöpft [exhausted], aber glücklich. Sie hatten eine Menge [a lot] über die moderne Welt gelernt und beschlossen [decided], Berlin zu ihrer neuen Heimat zu machen. Jeden Tag würden sie ein neues Abenteuer [adventure] erleben, aber für heute reichte es ihnen [it was enough for them], unter dem Sternenhimmel [starry sky] neben ihrer vertrauten [familiar] Spree zu schlafen."),
            StoryElement.Paragraph("Ende"),
        )
    ),
    Story(
        id = "5",
        title = "Der Tag, an dem Magie Wirklichkeit wurde [The day magic became reality] - A2",
        content = listOf(
            StoryElement.Paragraph("Einmal, an einem ganz normalen Morgen, geschah [happened] etwas Unglaubliches [Incredible]: Magie wurde plötzlich real! Die Leute auf der Straße konnten kaum glauben, was passierte. Sie sahen Kinder, die in der Luft schwebten [hovered], und Hunde, die sprechen konnten!"),
            StoryElement.Paragraph("In einem kleinen Dorf begann der Tag wie jeder andere. Aber dann fingen die Leute an [fingen etwas an = to start something], Zauber [Spells] zu wirken. Ein Junge verwandelte [transformed] seinen Apfel in Gold, und eine Frau ließ Blumen aus ihrer Hand wachsen [grew]."),
            StoryElement.Paragraph("Die Wissenschaftler waren verwirrt [confused] aber aufgeregt [excited]. Sie hatten immer gedacht, dass Magie nur in Märchen [Fairy Tales] existierte. Jetzt hatten sie die Chance, das Unmögliche zu erforschen."),
            StoryElement.Paragraph("Aber nicht alles war perfekt. Einige Leute benutzten die Magie, um Streiche [Pranks] zu spielen, und das verursachte [caused] Probleme. Autos flogen durch die Luft, und manche Gebäude änderten ihre Farben!"),
            StoryElement.Paragraph("Dann, genau so plötzlich, wie die Magie gekommen war, verschwand sie wieder. Am nächsten Tag war alles wieder normal. Die Menschen sprachen noch lange über den magischen Tag, aber niemand wusste, warum es passiert war oder ob es jemals wieder passieren würde."),
            StoryElement.Paragraph("Dieser Tag zeigte [showed] uns, dass das Leben voller Überraschungen [Surprises] ist. Manchmal geschehen Dinge, die wir uns nie hätten vorstellen können. Und vielleicht, nur vielleicht, ist Magie irgendwo da draußen, wartend auf den richtigen Moment, um wieder Wirklichkeit zu werden."),
            StoryElement.Paragraph("Ende"),
        )
    ),
    Story(
        id = "6",
        title = "Alter Baum [Old Tree] - A2",
        content = listOf(
            StoryElement.Paragraph("Es begann alles, als ich ein kleiner Samen [seed] war. Ein Vogel hatte mich weit weg von meinen Eltern getragen und mich in der weichen Erde [soil] versteckt. Bald begann ich zu wachsen. Die Sonne wärmte mich, und der Regen gab mir zu trinken."),
            StoryElement.Paragraph("Jahre gingen vorbei, und ich wurde größer und stärker. Kinder kamen und spielten unter meinen Ästen [branches], und ich hörte ihr Lachen. Manchmal kletterten sie hoch und bauten kleine Häuser in meinen Zweigen. Ich fühlte mich glücklich, ihnen Freude zu bringen."),
            StoryElement.Paragraph("Als ich älter wurde, veränderte sich die Welt um mich herum. Neue Gebäude wurden errichtet, und die Landschaft veränderte sich. Ich sah Pferde, die durch Autos ersetzt wurden, und die Lichter der Stadt wurden heller. Doch durch all die Veränderungen [changes] blieb ich ein stilles Zeugnis [witness] der Zeit."),
            StoryElement.Paragraph("In meinen besten Jahren war ich ein Zuhause für viele Vögel und kleine Tiere. Sie suchten Schutz [shelter] in meinem Laub [foliage] und ernährten [nourished] sich von den Früchten [fruits], die ich trug. Ich war stolz darauf, ein Teil des Lebenskreislaufs [cycle of life] zu sein."),
            StoryElement.Paragraph("Jetzt bin ich sehr alt. Meine Rinde [bark] ist rissig [cracked], und meine Äste sind nicht mehr so stark wie früher. Aber ich stehe immer noch hier, und ich biete Schatten [shade] und Trost [comfort] für diejenigen [those], die ihn suchen. Ich hoffe, dass ich noch viele Jahre hier stehen werde, um die Geschichten des Lebens zu erzählen."),
        )
    ),
    Story(
        id = "7",
        title = "Geisterflüstern und Herzschläge [Ghostly whispers and heartbeats] - A2",
        content = listOf(
            StoryElement.Paragraph("Marco rannte durch den Regen. Blitz [lightning] zuckte [flashed] am dunklen Himmel und Donner rollte [thunder rolled]. Er kam bei einem großen, alten Haus an. Es sah aus wie ein Schloss [castle], groß und ein wenig gruselig [spooky]. Aysha wartete auf ihn im Licht des Eingangs [entrance]."),
            StoryElement.Paragraph("Sie lächelte und winkte. \"Komm rein! Es ist nur ein Gewitter [thunderstorm].\""),
            StoryElement.Paragraph("Aber Marco wusste, dass es nicht nur das Gewitter war. In diesem Haus gab es Geister [ghosts]. Er hatte sie gehört und manchmal, in der Nacht, gesehen. Marco mochte keine Geister. Aber er mochte Aysha sehr."),
            StoryElement.Paragraph("Sie gingen zusammen in die Küche. Das Haus knarrte [creaked]. Aysha machte Tee und sie sprachen über normale Dinge: Arbeit, Bücher, Musik. Marco versuchte, die Geister zu vergessen."),
            StoryElement.Paragraph("Plötzlich hörten sie ein Geräusch [noise] oben. Aysha lachte. \"Ach, das ist nur das Haus. Es ist alt.\""),
            StoryElement.Paragraph("Marco sah sie an. Wie konnte sie keine Angst [fear] haben? Aysha sah seine Sorge [worry] und nahm seine Hand. \"Du gewöhnst [get used to] dich daran. Die Geister tun nichts. Sie sind ein Teil des Hauses.\""),
            StoryElement.Paragraph("Marco wollte mutig [brave] sein. \"Okay,\" sagte er und trank seinen Tee. Aber tief [deep] in seinem Herzen fühlte er, dass etwas nicht stimmte [wasn't right]. Trotzdem, er war verliebt [in love]. Und für Aysha würde er die Geister, das Gruseln [spookiness], ja sogar seine Angst akzeptieren [accept]."),
            StoryElement.Paragraph("Die Nacht kam und sie gingen ins Bett. Im dunklen Zimmer flüsterte [whispered] etwas Unbekanntes [unknown]. Marco hielt Ayshas Hand fest. Er würde bleiben. Aus Liebe. Aber er fragte sich, ob die Liebe genug war, um die Schatten zu besiegen [overcome the shadows]."),
        )
    ),
)
