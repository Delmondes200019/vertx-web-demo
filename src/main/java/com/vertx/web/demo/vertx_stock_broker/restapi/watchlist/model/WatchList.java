package com.vertx.web.demo.vertx_stock_broker.restapi.watchlist.model;

import com.vertx.web.demo.vertx_stock_broker.restapi.assets.model.Asset;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchList {

  private List<Asset> assets;

  public JsonObject toJsonObject(){
    return JsonObject.mapFrom(this);
  }
}
