package nus.iss.ssf.POApp.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.StackWalker.Option;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import nus.iss.ssf.POApp.model.Quotation;

@Service
public class QuotationService {

    private static final String URL = "https://quotation.chuklee.com%s";
    private static final String QUOTE_URL = "/quotation";

    public Optional<Quotation> getQuotations(List<String> items) {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (String i : items) {
            arrBuilder.add(i);
        }

        JsonArray arr = arrBuilder.build();

        String url = UriComponentsBuilder.fromUriString(URL.formatted(QUOTE_URL))
                .toUriString();

        RequestEntity<String> req = RequestEntity.post(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(arr.toString(), String.class);      
        
        RestTemplate template = new RestTemplate();

        ResponseEntity<String> resp = template.exchange(req, String.class);

        if(resp.getStatusCodeValue() >= 400)
        return Optional.empty();

        String quote = resp.getBody();
        InputStream is = new ByteArrayInputStream(quote.getBytes());
        JsonReader r = Json.createReader(is);
        JsonObject quotationObj = r.readObject();
    
        Quotation quotation = new Quotation();
        quotation.setQuoteId(quotationObj.getString("quoteId"));

        Map<String, Float> quoteMap = new HashMap<>();
        JsonArray quotations = quotationObj.getJsonArray("quotations");
        for (int i = 0; i < quotations.size(); i++) {
            JsonObject obj = quotations.getJsonObject(i);
            String itemName = obj.getString("item");
            JsonValue itemPrice = obj.getJsonNumber("unitPrice");
            Float unitPrice = Float.parseFloat(itemPrice.toString());

            quoteMap.put(itemName, unitPrice);
        }

        quotation.setQuotations(quoteMap);

        return Optional.of(quotation);
    }
    
}
