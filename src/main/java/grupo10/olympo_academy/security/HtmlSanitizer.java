package grupo10.olympo_academy.security;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class HtmlSanitizer {

    public String clean(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, Safelist.relaxed());
    }
}