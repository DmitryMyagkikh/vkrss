import groovy.xml.MarkupBuilder
import groovyx.net.http.HTTPBuilder
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import java.text.SimpleDateFormat

class VkWallParser {

    static String PUB_DATE_FORMAT = 'E, dd MMM yyyy HH:mm:ss Z'
    static String LINK_DATE_FORMAT = 'yyyy_MM_dd_HH_mm'
    static String TODAY = 'Today'
    static String YESTERDAY = 'Yesterday'

    SimpleDateFormat pubDateFormatter

    public VkWallParser(String url) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
        pubDateFormatter = new SimpleDateFormat(PUB_DATE_FORMAT, Locale.US)
    }

    String parse(String group) {
        String url = "https://vk.com/${group}"
        def http = new HTTPBuilder(url)

        def html = http.get([:])

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm a", Locale.US)
        SimpleDateFormat stringFormatter = new SimpleDateFormat("dd MMM", Locale.US)

        StringWriter writer = new StringWriter()
        MarkupBuilder xml = new MarkupBuilder(writer)
        Date today = new Date()
        Date yesterday = new Date() - 1

        xml.rss(version: "2.0", "xmlns:dc": "http://purl.org/dc/elements/1.1/") {
            channel() {
                title("VK Wall ${group}")
                link(url.toString())
                description("VK Wall ${group}")
                html."**".findAll { it.@class.toString().contains("wall_item") }.each { news ->
                    if (news) {
                        item() {
                            String pubDateString = news."**".find { it.name() == 'A' && it.@class.toString() == 'wi_date' }?.text()
                            String dateString = pubDateString.
                                    replace(TODAY, stringFormatter.format(today)).
                                    replace(YESTERDAY, stringFormatter.format(yesterday)).
                                    replace('at ', '')

                            Date date = sdf.parse(dateString)
                            date[Calendar.YEAR] = today[Calendar.YEAR]


                            pubDate(pubDateFormatter.format(date))
                            link("${url}/${date.format(LINK_DATE_FORMAT)}")

                            String mainNews = news."**".find { it.@class.toString().contains("wi_body") }.toString()
                            List<String> words = mainNews.split(' ') as List<String>
                            title(words[0..Math.min(7, words.size()) - 1].join(' '))

                            List<String> images = news."**".findAll { it.name() == 'IMG' }.collect { it.@"data-src_big".text() ? it.@"data-src_big".text().split('\\|')[0] : it.@src.text() }
                            images = images.findAll { it as boolean }
                            List<String> oneNews = [mainNews] + images.collect { "<img src='${it}'/>" }

                            description(oneNews.join('<br/>'))
                        }
                    }
                }
            }
        }


        println writer.toString()
        writer.toString()
//        return xml
    }

}
