/*******************************************************************************
 * Copyright (c) 1997-2009 by ProSyst Software GmbH
 * http://www.prosyst.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    ProSyst Software GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.ds.tests.tb13;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.eclipse.equinox.ds.tests.tbc.PropertiesProvider;
import org.eclipse.equinox.ds.tests.tbc.ComponentManager;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;


public class BindUnbindRegistrator implements PropertiesProvider {
  private Dictionary properties = new Properties();
  private ComponentContext ctxt;
  private static final int BIND_SR = 1 << 0;
  private static final int UNBIND_SR = 1 << 1;
  private static final int BIND_CM = 1 << 2;
  private static final int UNBIND_CM = 1 << 3;
  private static final int BIND_CM_MAP = 1 << 4;
  private static final int UNBIND_CM_MAP = 1 << 5;

  protected void activate(ComponentContext ctxt) {
    this.ctxt = ctxt;
    Dictionary props = ctxt.getProperties();
    Enumeration en = props.keys();
    while (en.hasMoreElements()) {
      Object key = en.nextElement();
      properties.put(key, props.get(key));
    }
  }

  protected void deactivate(ComponentContext ctxt) {

  }

  protected void bindSr(ServiceReference sr) {
    setDataBits(BIND_SR);
  }

  protected void unbindSr(ServiceReference sr) {
    setDataBits(UNBIND_SR);
  }

  protected void bindCm(ComponentManager ce) {
    setDataBits(BIND_CM);
  }

  protected void unbindCm(ComponentManager ce) {
    setDataBits(UNBIND_CM);
  }

  protected void bindCmMap(ComponentManager ce, Map props) {
    setDataBits(BIND_CM_MAP);
  }

  protected void unbindCmMap(ComponentManager ce, Map props) {
    setDataBits(UNBIND_CM_MAP);
  }

  public Dictionary getProperties() {
    return properties;
  }

  private void setDataBits(int value) {
    if (properties == null) {
      return;
    }
    Object prop = properties.get("config.base.data");
    int data = (prop instanceof Integer) ? ((Integer) prop).intValue() : 0;
    properties.put("config.base.data", new Integer(data | value));
  }

  public ComponentContext getComponentContext() {
    return ctxt;
  }
}