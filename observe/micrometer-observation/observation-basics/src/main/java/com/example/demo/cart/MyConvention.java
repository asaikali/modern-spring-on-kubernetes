package com.example.demo.cart;

import io.micrometer.common.KeyValues;
import io.micrometer.observation.Observation.Context;
import io.micrometer.observation.ObservationConvention;

public class MyConvention implements ObservationConvention<Context> {

  @Override
  public KeyValues getLowCardinalityKeyValues(Context context) {
    return ObservationConvention.super.getLowCardinalityKeyValues(context);
  }

  @Override
  public KeyValues getHighCardinalityKeyValues(Context context) {
    return ObservationConvention.super.getHighCardinalityKeyValues(context);
  }

  @Override
  public boolean supportsContext(Context context) {
    return false;
  }

  @Override
  public String getName() {
    return ObservationConvention.super.getName();
  }

  @Override
  public String getContextualName(Context context) {
    return ObservationConvention.super.getContextualName(context);
  }
}
