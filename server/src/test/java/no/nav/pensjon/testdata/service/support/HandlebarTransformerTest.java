package no.nav.pensjon.testdata.service.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class HandlebarTransformerTest {

    @Test
    void executeHappyCase() {
        final String newValue = "12345";
        String result = HandlebarTransformer.execute("SELECT * FROM TABLE WHERE ID='{id}'", Collections.singletonMap("id", newValue));
        Assert.assertFalse(result.contains("{"));
        Assert.assertFalse(result.contains("}"));
        Assert.assertTrue(result.contains(newValue));
    }

    @Test
    void executeWithInputtype() {
        final String newValue = "12345";
        String result = HandlebarTransformer.execute("SELECT * FROM TABLE WHERE ID='{id|number}'", Collections.singletonMap("id", newValue));
        Assert.assertFalse(result.contains("{"));
        Assert.assertFalse(result.contains("}"));
        Assert.assertTrue(result.contains(newValue));
    }

    @Test
    void executeWithInputtypeAndValidators() {
        final String newValue = "12345";
        String result = HandlebarTransformer.execute("SELECT * FROM TABLE WHERE ID='{id|number|validator}'", Collections.singletonMap("id", newValue));
        Assert.assertFalse(result.contains("{"));
        Assert.assertFalse(result.contains("}"));
        Assert.assertTrue(result.contains(newValue));
    }

    @Test
    void executeWithseveralHandlbars() {
        Map<String, String> handlebars = new HashMap<>();
        handlebars.put("id", "12345");
        handlebars.put("anotherid", "98765");
        String result = HandlebarTransformer.execute("SELECT * FROM TABLE WHERE ID='{id|number|validator}' AND anotherid ='{anotherid|number|validator}' ", handlebars);
        Assert.assertFalse(result.contains("{"));
        Assert.assertFalse(result.contains("}"));
        Assert.assertTrue(result.contains("12345"));
        Assert.assertTrue(result.contains("98765"));
    }

    @Test
    void executeWithseveralSameHandlbars() {
        Map<String, String> handlebars = new HashMap<>();
        handlebars.put("id", "12345");
        String result = HandlebarTransformer.execute("SELECT * FROM TABLE WHERE ID='{id|number|validator}' "
                + "AND second_id ='{id|number|validator}' "
                + "AND third_id ='{id|number|validator}' ", handlebars);
        Assert.assertFalse(result.contains("{"));
        Assert.assertFalse(result.contains("}"));
        Assert.assertTrue(result.contains("12345"));
    }

    @Test
    void executeWithNoHandlebars() {
        String sql = "SELECT WHATEVER FROM WHEREVER WHERE ID={id|number}";
        String result = HandlebarTransformer.execute(sql, Collections.emptyMap());
        Assert.assertEquals(result,sql);
    }
}