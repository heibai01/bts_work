package  com.led.player.service;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.Transformer;
 import javax.xml.transform.TransformerConfigurationException;
 import javax.xml.transform.TransformerException;
 import javax.xml.transform.TransformerFactory;
 import javax.xml.transform.dom.DOMSource;
 import javax.xml.transform.stream.StreamResult;
 import javax.xml.xpath.XPathConstants;
 import javax.xml.xpath.XPathExpressionException;
 import javax.xml.xpath.XPathFactory;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.xml.sax.SAXException;
 import org.apache.commons.lang.StringUtils;
 
 public final class DomUtils
 {
	 
	 protected static String logDirectory = getDefaultLogDirectory();
	 
   public static Document newDocument()
   {
     try
     {
       return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument(); 
     } 
     catch (ParserConfigurationException e) 
     {
    	 
     }
     return null;
   }
 
   //��ȡXml�ļ������ļ����ص�Document��
   public static Document load(String xmlFile)
   {
     try
     {
       return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(xmlFile));
     } 
     catch (ParserConfigurationException e) 
     {
       return null;
     }
     catch (IOException e) 
     {
    	 e.printStackTrace();
       return null; 
     } 
     catch (SAXException e) 
     {
     }
     return null;
   	}
 
   public static Document loadFromUri(String uri)
   {
     try
     {
       return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(uri);
     } 
     catch (ParserConfigurationException e) 
     {
       return null;
     }
     catch (IOException e) 
     {
       return null; 
     }
     catch (SAXException e) 
     {
     }
     return null;
   }
 
   //����������
   public static Document load(InputStream stream)
   {
     try
     {
       return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
     } 
     catch (ParserConfigurationException e) 
     {
       e.printStackTrace(System.out);
       return null;
     }
     catch (IOException e) 
     {
       e.printStackTrace(System.out);
       return null;
     }
     catch (SAXException e) 
     {
       e.printStackTrace(System.out);
     }
     return null;
   }
 
   //����xml�ַ�
   public static Document parseXmlString(String xml)
   {
     return parseXmlString(xml, null);
   }
 
   //��ݱ��뷽ʽ����xml�ַ�
   public static Document parseXmlString(String xml, String encoding)
   {
     ByteArrayInputStream input = null;
     try
     {
       input = new ByteArrayInputStream((encoding == null) ? xml.getBytes() : xml.getBytes("UTF-8"));
       return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
     }
     catch (UnsupportedEncodingException e) 
     {
       return null;
     }
     catch (IOException e) 
     {
       return null;
     }
     catch (ParserConfigurationException e) 
     {
       return null;
     }
     catch (SAXException e) 
     {
       return null;
     }
     
     finally 
     {
       if (input != null)
       try 
       {
           input.close();
       }
       catch (IOException localIOException6) 
       {
       
       }
      }
    }
 
   public static String toXmlString(Document doc) 
   {
     return toXmlString(doc, null);
   }
   
   //����ַ���뷽ʽ ��xmlתΪString
   public static String toXmlString(Document doc, String encoding)
   {
     if (doc == null) 
     {
       throw new IllegalArgumentException("invalid document!");
     }
     
     ByteArrayOutputStream byteStream = null;
     
     try
     {
       Transformer transform = TransformerFactory.newInstance().newTransformer();
       if (!StringUtils.isBlank(encoding)) 
       {
         transform.setOutputProperty("encoding", encoding);
       }
       byteStream = new ByteArrayOutputStream();
       transform.transform(new DOMSource(doc), new StreamResult(byteStream));
 
       String xml = null;
       if (!StringUtils.isBlank(encoding))
         xml = byteStream.toString();
       else {
         xml = byteStream.toString(encoding);
       }
       return xml;
     }
     catch (TransformerConfigurationException e) 
     {
       return null;
     }
     catch (TransformerException e) 
     {
       return null;
     }
     catch (UnsupportedEncodingException e) 
     {
       return null;
     } 
     finally 
     {
       if (byteStream != null)
       try 
       {
           byteStream.close();
       }
       catch (IOException localIOException4) 
       {
       }
     }
   }
 
   public static boolean save(String filePath, Document doc)
   {
     return save(filePath, doc, null);
   }
 
   //���浽�ļ�
   public static boolean save(String filePath, Document doc, String encoding)
   {
     if (doc == null) {
       throw new IllegalArgumentException("invalid document!");
     }
     FileOutputStream fileStream = null;
     try
     {
       Transformer transform = TransformerFactory.newInstance().newTransformer();
       transform.setOutputProperty("indent", "yes");
       if (!StringUtils.isBlank(encoding))
       {
         transform.setOutputProperty("encoding", encoding);
       }
       File file = new File(filePath);
       file.getParentFile().mkdirs();
       if (!file.exists()) 
       {
         file.createNewFile();
       }
       fileStream = new FileOutputStream(file);
 
       transform.transform(new DOMSource(doc), new StreamResult(fileStream));
 
       return true;
     } catch (TransformerConfigurationException e) {
       return false;
     } catch (TransformerException e) {
       return false;
     } catch (IOException e) {
       return false;
     } finally {
       if (fileStream != null)
         try {
           fileStream.close();
         }
         catch (IOException localIOException5) {
         }
     }
   }
 
   /**
    * ��ȡxml�ļ������нڵ�
    * @param node
    * @param xpath
    * @return
    */
   public static NodeList selectNodes(Node node, String xpath) 
   {
     try
     {
       return (NodeList)XPathFactory.newInstance().newXPath().evaluate(xpath, node, XPathConstants.NODESET); 
     }
     catch (XPathExpressionException e)
     {
    	 
     }
     return null;
   }
 
   /**
    * ��ȡ�ļ��ĵ����ڵ�
    * @param node
    * @param xpath
    * @return
    */
   
   public static Node selectSingleNode(Node node, String xpath)
   {
     try 
     {
       return (Node)XPathFactory.newInstance().newXPath().evaluate(xpath, node, XPathConstants.NODE); 
     }
     catch (XPathExpressionException e) 
     {
     }
     return null;
   }
 
   public static int getAttributeInt(Element elem, String name)
   {
     return getAttributeInt(elem, name, 0);
   }
 
   public static int getAttributeInt(Element elem, String name, int defaultVal) {
     return getAttributeInt(elem, name, defaultVal, 10);
   }
 
   public static int getAttributeInt(Element elem, int radix, String name)
   {
     return getAttributeInt(elem, name, 0, radix);
   }
 
   public static int getAttributeInt(Element elem, String name, int defaultVal, int radix)
   {
     try 
     {
       return (elem.hasAttribute(name)) ? Integer.parseInt(StringUtils.trimToEmpty(elem.getAttribute(name)), radix) : 
         defaultVal;
     }
     catch (Exception e) 
     {
     }
     return defaultVal;
   }
 
   public static long getAttributeLong(Element elem, String name)
   {
     return getAttributeLong(elem, name, 0L);
   }
 
   public static long getAttributeLong(Element elem, String name, long defaultVal) {
     return getAttributeLong(elem, name, defaultVal, 10);
   }
 
   public static long getAttributeLong(Element elem, int radix, String name)
   {
     return getAttributeLong(elem, name, 0L, radix);
   }
 
   public static long getAttributeLong(Element elem, String name, long defaultVal, int radix)
   {
     try {
       return (elem.hasAttribute(name)) ? Long.parseLong(
         StringUtils.trimToEmpty(elem.getAttribute(name)), radix) : 
         defaultVal;
     } catch (Exception e) {
     }
     return defaultVal;
   }
 
   public static String getAttributeString(Element elem, String name)
   {
     return getAttributeString(elem, name, "");
   }
 
   public static String getAttributeString(Element elem, String name, String defaultVal) {
     try 
     {
       return (elem.hasAttribute(name)) ? elem.getAttribute(name) : defaultVal;
     }
     catch (Exception e) 
     {
     }
     return defaultVal;
   }
 
   public static Boolean getAttributeBoolean(Element elem, String name)
   {
     return getAttributeBoolean(elem, name, Boolean.valueOf(false));
   }
 
   public static Boolean getAttributeBoolean(Element elem, String name, Boolean defaultVal)
   {
     try {
       return Boolean.valueOf((elem.hasAttribute(name)) ? Boolean.parseBoolean(StringUtils.trimToEmpty(elem.getAttribute(name))) : defaultVal.booleanValue());
     }
     catch (Exception e) 
     {
     }
     return defaultVal;
   }
 
   public static String getTextContent(Element elem, String xpath)
   {
     return getTextContent(elem, xpath, null);
   }
 
   /**
    * ��ȡĳ���ڵ���ı�ֵ
    * @param elem
    * @param xpath
    * @param defaultVal
    * @return
    */
   
   public static String getTextContent(Element elem, String xpath, String defaultVal) 
   {
     try 
     {
       Node node = selectSingleNode(elem, xpath);
       if (node != null) 
       {
         String textContent = node.getTextContent();
         return (textContent != null) ? textContent : defaultVal;
       }
       return defaultVal;
     }
     catch (Exception e) 
     {
     }
     return defaultVal;
   }
   
   
   public static String getDefaultLogDirectory()
   {
	 String strPath="";
	 String strExPath="";
     try
     {
       //String classDirectory =UtilTools.class.getClassLoader().getResource("/").getPath();
       String classDirectory =DomUtils.class.getClass().getResource("/").getPath(); 
       classDirectory = URLDecoder.decode(classDirectory, "UTF-8");
       File f = new File(classDirectory);
       f = f.getParentFile().getParentFile();
      strPath = "mnt" + File.separatorChar + "sdcard" +  File.separatorChar  + "Download" +File.separatorChar;
      strExPath = "mnt" + File.separatorChar + "sdcard" +  File.separatorChar  + "program" +File.separatorChar;
        //strPath = "mnt" + File.separatorChar + "extsd" +  File.separatorChar  + "program" +File.separatorChar;
        //strExPath = "mnt" + File.separatorChar + "extsd" +  File.separatorChar  + "program" +File.separatorChar;
        
      } 
      catch (Exception ex) 
      {
      }
      return strPath;
   }
 }

