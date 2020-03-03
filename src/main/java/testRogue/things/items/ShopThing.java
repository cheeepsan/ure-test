package testRogue.things.items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ure.things.UThing;

public class ShopThing extends UThing {

/**
 *   {
 *     "id": 1,
 *     "categoryId": 1,
 *     "name": "item 1",
 *     "description": "just an item",
 *     "price": 100
 *   },
 */

    public Integer id;
    public Integer categoryId;
    public String name;
    public String description;
    public String price;

    @JsonCreator
    public ShopThing(
            @JsonProperty("id")             Integer id,
            @JsonProperty("categoryId")     Integer categoryId,
            @JsonProperty("name")           String name,
            @JsonProperty("description")    String description,
            @JsonProperty("price")          String price
    ) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
    }
    public ShopThing() {

    }

}
