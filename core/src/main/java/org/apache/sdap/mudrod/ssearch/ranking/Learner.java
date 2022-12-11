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
package org.apache.sdap.mudrod.ssearch.ranking;

import org.apache.sdap.mudrod.driver.SparkDriver;
import org.apache.spark.SparkContext;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.regression.LabeledPoint;

import java.io.Serializable;

/**
 * Supports the ability to importing classifier into memory
 */
public class Learner implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private SVMModel model = null;
  private transient SparkContext sc = null;

  /**
   * Constructor to load in spark SVM classifier
   *
   * @param skd            an instance of spark driver
   * @param svmSgdModel    path to a trained model
   */
  public Learner(SparkDriver skd, String svmSgdModel) {
      sc = skd.sc.sc();
      sc.addFile(svmSgdModel, true);
      model = SVMModel.load(sc, svmSgdModel);
  }

  /**
   * Method of classifying instance
   *
   * @param p the instance that needs to be classified
   * @return the class id
   */
  public double classify(LabeledPoint p) {
    return model.predict(p.features());
  }

}
