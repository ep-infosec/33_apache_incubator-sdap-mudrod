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
package org.apache.sdap.mudrod.discoveryengine;

import org.apache.sdap.mudrod.driver.ESDriver;
import org.apache.sdap.mudrod.driver.SparkDriver;
import org.apache.sdap.mudrod.recommendation.pre.ImportMetadata;
import org.apache.sdap.mudrod.recommendation.pre.MetadataTFIDFGenerator;
import org.apache.sdap.mudrod.recommendation.pre.NormalizeFeatures;
import org.apache.sdap.mudrod.recommendation.pre.SessionCooccurence;
import org.apache.sdap.mudrod.recommendation.process.AbstractBasedSimilarity;
import org.apache.sdap.mudrod.recommendation.process.FeatureBasedSimilarity;
import org.apache.sdap.mudrod.recommendation.process.SessionBasedCF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class RecommendEngine extends DiscoveryEngineAbstract {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(RecommendEngine.class);

  public RecommendEngine(Properties props, ESDriver es, SparkDriver spark) {
    super(props, es, spark);
    LOG.info("Started Mudrod Recommend Engine.");
  }

  @Override
  public void preprocess() {
    LOG.info("Recommendation preprocessing starts.");

    startTime = System.currentTimeMillis();

    DiscoveryStepAbstract harvester = new ImportMetadata(this.props, this.es, this.spark);
    harvester.execute();

    DiscoveryStepAbstract tfidf = new MetadataTFIDFGenerator(this.props, this.es, this.spark);
    tfidf.execute();

    DiscoveryStepAbstract sessionMatrixGen = new SessionCooccurence(this.props, this.es, this.spark);
    sessionMatrixGen.execute();

    DiscoveryStepAbstract transformer = new NormalizeFeatures(this.props, this.es, this.spark);
    transformer.execute();

    endTime = System.currentTimeMillis();

    LOG.info("Recommendation preprocessing ends. Took {}s", (endTime - startTime) / 1000);
  }

  @Override
  public void process() {
    // TODO Auto-generated method stub
    LOG.info("Recommendation processing starts.");

    startTime = System.currentTimeMillis();

    DiscoveryStepAbstract tfCF = new AbstractBasedSimilarity(this.props, this.es, this.spark);
    tfCF.execute();

    DiscoveryStepAbstract cbCF = new FeatureBasedSimilarity(this.props, this.es, this.spark);
    cbCF.execute();

    DiscoveryStepAbstract sbCF = new SessionBasedCF(this.props, this.es, this.spark);
    sbCF.execute();

    endTime = System.currentTimeMillis();

    LOG.info("Recommendation processing ends. Took {}s", (endTime - startTime) / 1000);
  }

  @Override
  public void output() {

  }

}
