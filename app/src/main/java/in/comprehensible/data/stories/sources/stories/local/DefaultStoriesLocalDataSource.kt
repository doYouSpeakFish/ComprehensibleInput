package `in`.comprehensible.data.stories.sources.stories.local

import `in`.comprehensible.data.stories.model.Story
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Default implementation of [StoriesLocalDataSource] that provides the story content.
 */
class DefaultStoriesLocalDataSource(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : StoriesLocalDataSource {
    override suspend fun getStory() = withContext(dispatcher) {
        Story(
            title = "Geheimes Heilbad [Secret Spa] - A2",
            content = """
            Maria lebt in einem kleinen Dorf. Jeden Morgen geht sie spazieren im Wald neben ihrem Haus. Sie liebt die Ruhe und die frische Luft.

            Eines Tages hört sie das Geräusch von fließendem [flowing] Wasser. Sie folgt dem Geräusch und entdeckt eine Quelle [Spring]. Das Wasser ist warm.
            
            „Ein Heilbad!“, denkt sie. Ein Heilbad ist ein natürliches Wasserbecken mit warmem Wasser. Viele Menschen glauben, dass solches Wasser heilende Kräfte [healing powers] hat.

            Maria hat eine Idee. Sie könnte hierher kommen und sich entspannen [relax]. Vielleicht könnte sie sogar ihre Freunde einladen [invite].

            Aber dann sieht sie etwas am Boden des Heilbades. Es ist ein kleiner, glänzender Gegenstand [shiny object]. Maria nimmt es hoch. Es ist eine alte Münze [coin].

            Plötzlich [suddenly] hört sie eine Stimme [voice] hinter sich: „Das ist meine Münze!“ Maria dreht sich um [Maria turned around] und sieht eine alte Frau. Die Frau sieht freundlich aus, aber ihre Augen sind traurig [sad].

            „Dieses Heilbad gehört [belongs to] meiner Familie“, sagt die alte Frau. „Ich habe diese Münze vor vielen Jahren hier verloren [lost here long ago].“

            Maria fühlt sich schlecht. Sie gibt der Frau die Münze zurück und entschuldigt sich.

            Die alte Frau lächelt [smiled]. „Es ist in Ordnung“, sagt sie. „Aber dieses Heilbad hat ein Geheimnis [secret]. Es kann nur von denjenigen [those] gesehen werden, die reinen Herzens [pure of heart] sind.“

            Maria ist überrascht [surprised]. Sie dankt der Frau und verlässt [left] das Heilbad.

            Am nächsten Tag bringt Maria ihre Freunde mit in den Wald. Aber sie können das Heilbad nicht finden. Es ist verschwunden [disappeared].

            Maria lächelt. Sie weiß jetzt, dass einige Dinge im Leben nur für sie bestimmt sind [She now knows that some things in life are meant only for her].

            Ende.
        """.trimIndent()
        )
    }
}
