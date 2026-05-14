package grupo10.olympo_academy.security;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class HtmlSanitizer {

    public static String clean(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, Safelist.relaxed());
    }

    public static List<String> cleanList(List<String> list) {
        if (list == null) return null;

    return list.stream().map(HtmlSanitizer::clean).toList();
}
}