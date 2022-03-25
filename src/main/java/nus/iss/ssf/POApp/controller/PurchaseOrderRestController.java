package nus.iss.ssf.POApp.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import nus.iss.ssf.POApp.model.Quotation;
import nus.iss.ssf.POApp.service.QuotationService;

@RestController
@RequestMapping(path="/api")
public class PurchaseOrderRestController {

    @Autowired
    private QuotationService quoteSvc;

    @PostMapping (path="/po", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postOrder(@RequestBody String payload) throws IOException {
        
        JsonObject req;
        try (InputStream is = new ByteArrayInputStream(payload.getBytes())){
        JsonReader reader = Json.createReader(is);
        req = reader.readObject();
        }
        
        System.out.println(">>>>>: " + req);
        String name = req.getString("name");
        JsonArray array = req.getJsonArray("lineItems");

        List<String> itemName = new ArrayList<>();


        
        for (int i=0; i < array.size(); i ++) {
            JsonObject item = array.getJsonObject(i);
            itemName.add(item.getString("item"));
        }
        
        
        Optional<Quotation> quotationOrder = quoteSvc.getQuotations(itemName);
        Quotation quotation = quotationOrder.get();

        if (quotationOrder.isEmpty()){
            return ResponseEntity.badRequest().body("{}");
        }

        Float totalPrice = 0f;

        for (int i=0; i < array.size(); i++) {
            JsonObject eachItem = array.getJsonObject(i);
            int quantity = eachItem.getInt("quantity");
            Float unitPrice = quotation.getQuotation(eachItem.getString("item"));
            totalPrice += quantity * unitPrice;
        }
       
        JsonObject result = Json.createObjectBuilder()
                                .add("invoiceId", quotation.getQuoteId())
                                .add("name", name)
                                .add("total", totalPrice)
                                .build();
    
      return ResponseEntity.ok(result.toString());
    
    }

}
