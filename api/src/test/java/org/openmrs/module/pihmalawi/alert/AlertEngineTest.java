/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.pihmalawi.alert;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;

import javax.script.ScriptEngine;
import java.util.*;

/**
 * Tests the AlertEngine
 */
public class AlertEngineTest {

    AlertEngine engine = new AlertEngine();

    @Test
    public void shouldTestFunctionsAndConstants() throws Exception {

        test("yearsBetween", 2000, 3, 1, 1980, 2, 1, 20);
        test("yearsBetween", 2000, 3, 1, 1980, 3, 1, 20);
        test("yearsBetween", 2000, 3, 1, 1980, 3, 2, 19);
        test("yearsBetween", 2000, 3, 1, 1980, 2, 28, 20);
        test("yearsBetween", 2000, 1, 1, 1999, 12, 31, 0);

        test("monthsBetween", 2005, 3, 5, 2000, 2, 5, 61);
        test("monthsBetween", 2005, 3, 5, 2000, 12, 6, 50);
        test("monthsBetween", 2005, 3, 5, 2000, 10, 4, 53);

        test("daysBetween", 2020, 3, 1, 2020, 2, 1, 29);
        test("daysBetween", 2021, 3, 1, 2021, 2, 1, 28);
        test("daysBetween", 2005, 1, 31, 2004, 10, 24, 99);

        ScriptEngine scriptEngine = engine.createScriptEngine(ObjectUtil.toMap("hello", "world"));

        Assert.assertEquals(Boolean.TRUE, scriptEngine.eval("missing()"));
        Assert.assertEquals(Boolean.TRUE, scriptEngine.eval("missing(null)"));
        Assert.assertEquals(Boolean.TRUE, scriptEngine.eval("missing(\"\")"));
        Assert.assertEquals(Boolean.TRUE, scriptEngine.eval("missing('')"));
        Assert.assertEquals(Boolean.FALSE, scriptEngine.eval("missing('hello')"));
        Assert.assertEquals(Boolean.FALSE, scriptEngine.eval("missing(hello)"));
        Assert.assertEquals(Boolean.TRUE, scriptEngine.eval("has([1, 2, 3], 1)"));
        Assert.assertEquals(Boolean.FALSE, scriptEngine.eval("has([1, 2, 3], 4)"));

        Assert.assertNotNull(scriptEngine.eval("active_art"));
    }

    @Test
    public void shouldTestHasAnyFunction() throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>();

        List<String> symptomsList = Arrays.asList("656f10da-977f-11e1-8993-905e29aff6c1", "654a56be-977f-11e1-8993-905e29aff6c1");

        String[] tb_symptoms = new String[] {"fever", "nightSweats", "cough"};
        List<String> tbSymptomsList = Arrays.asList("656f10da-977f-11e1-8993-905e29aff6c1", "654a56be-977f-11e1-8993-905e29aff6c1");

        variables.put("current_symptoms", symptomsList); //nightSweats, weightLoss

        ScriptEngine scriptEngine = engine.createScriptEngine(variables);
        Object res = scriptEngine.eval( "hasAny(current_symptoms, " +
                "['656f10da-977f-11e1-8993-905e29aff6c1', '654a56be-977f-11e1-8993-905e29aff6c1'])");

        Assert.assertEquals(true, res);

    }

    @Test
    public void shouldTestChronicCareDiagnoses() throws Exception {

        class ChronicDiagnosis {
            Date date = null;
            String value = null;

            public ChronicDiagnosis(Date date, String value) {
                this.date = date;
                this.value = value;
            }

            public Date getDate() {
                return date;
            }

            public void setDate(Date date) {
                this.date = date;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
        Map<String, Object> variables = new HashMap<String, Object>();
        //diabetes, hypertension
        List<ChronicDiagnosis> chronicDiagnosisList = Arrays.asList(
                new ChronicDiagnosis(DateUtil.getDateTime(2017, 4, 12), "6567426a-977f-11e1-8993-905e29aff6c1"),
                new ChronicDiagnosis(DateUtil.getDateTime(2018, 8, 17), "654abfc8-977f-11e1-8993-905e29aff6c1"));

        List<HashMap<String, String>> hashMapList = Arrays.asList(new HashMap<String, String>() {{
            put("date", "1265086800000");
            put("value", "6567426a-977f-11e1-8993-905e29aff6c1");
        }}, new HashMap<String, String>() {{
            put("date", "1265086800000");
            put("value", "654abfc8-977f-11e1-8993-905e29aff6c1");
        }});
        variables.put("chronic_care_diagnoses", hashMapList);
        ScriptEngine scriptEngine = engine.createScriptEngine(variables);

        Object res = scriptEngine.eval( "hasChronicCareDiagnosis(chronic_care_diagnoses, " +
                "['6567426a-977f-11e1-8993-905e29aff6c1', '65714206-977f-11e1-8993-905e29aff6c1'])"); //diabetes, diabetes_type_1

        Assert.assertEquals(true, res);

    }

    protected void test(String function, int y1, int m1, int d1, int y2, int m2, int d2, double expected) throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("t1", DateUtil.getDateTime(y1, m1, d1).getTime());
        variables.put("t2", DateUtil.getDateTime(y2, m2, d2).getTime());
        Object res = engine.createScriptEngine(variables).eval(function + "(t1, t2)");
        Assert.assertEquals(expected, res);
    }

    @Test
    public void shouldTestLoadingAlerts() throws Exception {
        List<AlertDefinition> alertDefinitions = engine.getAlertDefinitions();
        Assert.assertTrue(alertDefinitions.size() > 0);
    }
}
