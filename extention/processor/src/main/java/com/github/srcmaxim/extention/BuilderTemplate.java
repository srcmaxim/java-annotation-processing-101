package com.github.srcmaxim.extention;

import java.util.List;

public interface BuilderTemplate {

  CharSequence build(Builder builder);

  record Builder(String annotatedClassPackage, String annotatedClassName, String builderClassName, List<Field> fields) {

  }

  record Field(String type, String name) {

  }


}
