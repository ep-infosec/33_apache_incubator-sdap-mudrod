/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sdap.mudrod.services.autocomplete;

import com.google.gson.Gson;

import org.apache.sdap.mudrod.main.MudrodConstants;
import org.apache.sdap.mudrod.main.MudrodEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * An AutoCompleteResource for term autocompletion suggestion.
 */
@Path("/autocomplete")
public class AutoCompleteResource {

  private static final Logger LOG = LoggerFactory.getLogger(AutoCompleteResource.class);
  private MudrodEngine mEngine;

  public AutoCompleteResource(@Context ServletContext sc) {
    this.mEngine = (MudrodEngine) sc.getAttribute("MudrodInstance");
  }

  @GET
  @Path("/status")
  @Produces("text/html")
  public Response status() {
    return Response.ok("<h1>This is MUDROD AutoCompleteResource: running correctly...</h1>").build();
  }

  @GET
  @Path("/query")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes("text/plain")
  public Response autoComplete(@QueryParam("term") String term) {
    List<AutoCompleteData> result = new ArrayList<>();
    List<String> suggestList = mEngine.getESDriver().autoComplete(mEngine.getConfig().getProperty(MudrodConstants.ES_INDEX_NAME), term);
    for (final String item : suggestList) {
      result.add(new AutoCompleteData(item, item));
    }
    String json = new Gson().toJson(result);
    LOG.info("Response received: {}", json);
    return Response.ok(json, MediaType.APPLICATION_JSON).build();
  }

}
