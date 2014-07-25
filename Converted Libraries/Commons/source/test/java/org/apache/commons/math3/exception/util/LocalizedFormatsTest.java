package org.apache.commons.math3.exception.util;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;
public class LocalizedFormatsTest {
  @Test public void testMessageNumber(){
    Assert.assertEquals(313,LocalizedFormats.values().length);
  }
  @Test public void testAllKeysPresentInPropertiesFiles(){
    final String path=LocalizedFormats.class.getName().replaceAll("\\.","/");
    for (    final String language : new String[]{"fr"}) {
      ResourceBundle bundle=ResourceBundle.getBundle("assets/" + path,new Locale(language));
      for (      LocalizedFormats message : LocalizedFormats.values()) {
        final String messageKey=message.toString();
        boolean keyPresent=false;
        for (final Enumeration<String> keys=bundle.getKeys(); keys.hasMoreElements(); ) {
          keyPresent|=messageKey.equals(keys.nextElement());
        }
        Assert.assertTrue("missing key \"" + message.name() + "\" for language "+ language,keyPresent);
      }
      Assert.assertEquals(language,bundle.getLocale().getLanguage());
    }
  }
  @Test public void testAllPropertiesCorrespondToKeys(){
    final String path=LocalizedFormats.class.getName().replaceAll("\\.","/");
    for (    final String language : new String[]{"fr"}) {
      ResourceBundle bundle=ResourceBundle.getBundle("assets/" + path,new Locale(language));
      for (final Enumeration<String> keys=bundle.getKeys(); keys.hasMoreElements(); ) {
        final String propertyKey=keys.nextElement();
        try {
          Assert.assertNotNull(LocalizedFormats.valueOf(propertyKey));
        }
 catch (        IllegalArgumentException iae) {
          Assert.fail("unknown key \"" + propertyKey + "\" in language "+ language);
        }
      }
      Assert.assertEquals(language,bundle.getLocale().getLanguage());
    }
  }
  @Test public void testNoMissingFrenchTranslation(){
    for (    LocalizedFormats message : LocalizedFormats.values()) {
      String translated=message.getLocalizedString(Locale.FRENCH);
      Assert.assertFalse(message.name(),translated.toLowerCase().contains("missing translation"));
    }
  }
  @Test public void testNoOpEnglishTranslation(){
    for (    LocalizedFormats message : LocalizedFormats.values()) {
      String translated=message.getLocalizedString(Locale.ENGLISH);
      Assert.assertEquals(message.getSourceString(),translated);
    }
  }
  @Test public void testVariablePartsConsistency(){
    for (    final String language : new String[]{"fr"}) {
      Locale locale=new Locale(language);
      for (      LocalizedFormats message : LocalizedFormats.values()) {
        MessageFormat source=new MessageFormat(message.getSourceString());
        MessageFormat translated=new MessageFormat(message.getLocalizedString(locale));
        Assert.assertEquals(message.name() + " (" + language+ ")",source.getFormatsByArgumentIndex().length,translated.getFormatsByArgumentIndex().length);
      }
    }
  }
}
