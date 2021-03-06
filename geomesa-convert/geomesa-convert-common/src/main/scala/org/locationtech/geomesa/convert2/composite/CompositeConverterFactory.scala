/***********************************************************************
 * Copyright (c) 2013-2018 Commonwealth Computer Research, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 ***********************************************************************/

package org.locationtech.geomesa.convert2.composite

import com.typesafe.config.Config
import org.locationtech.geomesa.convert2.transforms.{Predicate, PredicateParser}
import org.locationtech.geomesa.convert2.{SimpleFeatureConverter, SimpleFeatureConverterFactory}
import org.opengis.feature.simple.SimpleFeatureType

class CompositeConverterFactory extends SimpleFeatureConverterFactory {

  import scala.collection.JavaConverters._

  override def apply(sft: SimpleFeatureType, conf: Config): Option[SimpleFeatureConverter] = {
    if (!conf.hasPath("type") || conf.getString("type") != "composite-converter") { None } else {
      val converters: Seq[(Predicate, SimpleFeatureConverter)] =
        conf.getConfigList("converters").asScala.map { c =>
          val pred = Predicate(c.getString("predicate"))
          val converter = SimpleFeatureConverter(sft, conf.getConfig(c.getString("converter")))
          (pred, converter)
        }
      Some(new CompositeConverter(sft, converters))
    }
  }
}
