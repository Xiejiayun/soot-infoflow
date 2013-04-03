package soot.jimple.infoflow.test.securibench;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;

import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowResults;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.IEntryPointCreator;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

public abstract class JUnitTests {

    protected static String path;
    protected static List<String> sources;
    protected static List<String> sinks;
    protected static final String[] sinkArray = new String[]{ "<java.io.PrintWriter: void println(java.lang.String)>",
    	"<java.io.PrintWriter: void println(java.lang.Object)>",
    	"<java.sql.Connection: java.sql.PreparedStatement prepareStatement(java.lang.String)>",
    	"<java.sql.Statement: boolean execute(java.lang.String)>",
    	"<java.sql.Statement: int executeUpdate(java.lang.String)>",
    	"<java.sql.Statement: int executeUpdate(java.lang.String,int)>",
    	"<java.sql.Statement: int executeUpdate(java.lang.String,java.lang.String[])>",
    	"<java.sql.Statement: java.sql.ResultSet executeQuery(java.lang.String)>",
    	"<javax.servlet.http.HttpServletResponse: void sendRedirect(java.lang.String)>",
    	"<java.io.File: void <init>(java.lang.String)>",
    	"<java.io.FileWriter: void <init>(java.lang.String)>",
    	"<java.io.FileInputStream: void <init>(java.lang.String)>"};
    
    
    protected static final String[] sourceArray = new String[]{
    	"<javax.servlet.ServletRequest: java.lang.String getParameter(java.lang.String)>",
    	"<javax.servlet.http.HttpServletRequest: java.lang.String getParameter(java.lang.String)>",
    	"<javax.servlet.ServletRequest: java.lang.String[] getParameterValues(java.lang.String)>",
    	"<javax.servlet.http.HttpServletRequest: java.lang.String[] getParameterValues(java.lang.String)>",
    	"<javax.servlet.ServletRequest: java.util.Map getParameterMap()>",
    	"<javax.servlet.http.HttpServletRequest: java.util.Map getParameterMap()>",	
    	"<javax.servlet.ServletConfig: java.lang.String getInitParameter(java.lang.String)>",
    	"<soot.jimple.infoflow.test.securibench.supportClasses.DummyServletConfig: java.lang.String getInitParameter(java.lang.String)>",
    	"<javax.servlet.ServletConfig: java.util.Enumeration getInitParameterNames()>",
    	"<javax.servlet.ServletContext: java.lang.String getInitParameter(java.lang.String)>",
    	"<javax.servlet.http.HttpServletRequest: java.lang.String getParameter(java.lang.String)>",
    	"<javax.servlet.http.HttpServletRequest: java.lang.String[] getParameterValues(java.lang.String)>",
    	"<javax.servlet.http.HttpServletRequest: java.util.Map getParameterMap()>",	
    	"<javax.servlet.http.HttpServletRequest: javax.servlet.http.Cookie[] getCookies()>",
    	"<javax.servlet.http.HttpServletRequest: java.lang.String getHeader(java.lang.String)>",
    	"<javax.servlet.http.HttpServletRequest: java.util.Enumeration getHeaders(java.lang.String)>",
    	"<javax.servlet.http.HttpServletRequest: java.util.Enumeration getHeaderNames()>",
    	"<javax.servlet.http.HttpServletRequest: java.lang.String getProtocol()>",
    	"<javax.servlet.http.HttpServletRequest: java.lang.String getScheme()>", 
    	"<javax.servlet.http.HttpServletRequest: java.lang.String getAuthType()>",
    	"<javax.servlet.http.HttpServletRequest: java.lang.String getQueryString()>",
    	"<javax.servlet.http.HttpServletRequest: java.lang.String getRemoteUser()>",
    	"<javax.servlet.http.HttpServletRequest: java.lang.StringBuffer getRequestURL()>",
    	"<javax.servlet.http.HttpServletRequest: javax.servlet.ServletInputStream getInputStream()>",
    	"<javax.servlet.ServletRequest: javax.servlet.ServletInputStream getInputStream()>",
    	"<com.oreilly.servlet.MultipartRequest: java.lang.String getParameter(java.lang.String)>"};
    
    protected static boolean local = false;
    protected static boolean taintWrapper = false;
    protected static boolean substituteCallParams = true;
    
    protected IEntryPointCreator entryPointCreator = null;
   
    @BeforeClass
    public static void setUp() throws IOException
    {
    	 File f = new File(".");
    	 path = //System.getProperty("java.home")+ File.separator + "lib"+File.separator + "jce.jar" + System.getProperty("path.separator") +
    	 		System.getProperty("java.home")+ File.separator + "lib"+File.separator + "rt.jar"+ System.getProperty("path.separator") +
    			 f.getCanonicalPath() + File.separator + "bin"+ System.getProperty("path.separator") + 
    			 f.getCanonicalPath()+ File.separator+ "lib"+ File.separator+ "j2ee.jar" +
    			 System.getProperty("path.separator") + 
    			 f.getCanonicalPath()+ File.separator+ "lib"+ File.separator+ "cos.jar";
        System.out.println("Using following locations as sources for classes: " + path);
    	sources = Arrays.asList(sourceArray);
        sinks = Arrays.asList(sinkArray);
    }
    
    @Before
    public void resetSootAndStream() throws IOException{
    	 soot.G.reset();
    	 System.gc();
    	 
    }
    
    protected void checkInfoflow(Infoflow infoflow){
		  if(infoflow.isResultAvailable()){
				InfoflowResults map = infoflow.getResults();
				boolean containsSink = false;
				List<String> actualSinkStrings = new LinkedList<String>();
				for(String sink : sinkArray){
					if(map.containsSinkMethod(sink)){
						containsSink = true;
						actualSinkStrings.add(sink); 
					}
				}
				
				assertTrue(containsSink);
				boolean onePathFound = false;
				for(String sink : actualSinkStrings){
					boolean hasPath = false;
					for(String source : sourceArray){
						if(map.isPathBetweenMethods(sink, source)){
							hasPath = true;
							break;
						}
					}
					if(hasPath){
						onePathFound = true;
					}
				}
				assertTrue(onePathFound);
				
			}else{
				fail("result is not available");
			}
	  }
    
    protected void negativeCheckInfoflow(Infoflow infoflow){
		  if(infoflow.isResultAvailable()){
				InfoflowResults map = infoflow.getResults();
				for(String sink : sinkArray){
					if(map.containsSinkMethod(sink)){
						fail("sink is reached: " +sink);
					}
				}
			}else{
				fail("result is not available");
			}
	  }
    
    protected Infoflow initInfoflow(){
    	List<String> substClasses = new LinkedList<String>();
    	substClasses.add("soot.jimple.infoflow.test.securibench.supportClasses.DummyHttpRequest");
    	substClasses.add("soot.jimple.infoflow.test.securibench.supportClasses.DummyHttpResponse");

    	DefaultEntryPointCreator entryPointCreator = new DefaultEntryPointCreator();
    	entryPointCreator.setSubstituteCallParams(substituteCallParams);
    	entryPointCreator.setSubstituteClasses(substClasses);
    	this.entryPointCreator = entryPointCreator;

    	Infoflow result = new Infoflow();
    	result.setLocalInfoflow(local);
    	SootConfigSecuriBench testConfig = new SootConfigSecuriBench();
    	result.setSootConfig(testConfig);
    	if(taintWrapper){
    		EasyTaintWrapper easyWrapper;
			try {
				easyWrapper = new EasyTaintWrapper(new File("EasyTaintWrapperSource.txt"));
				result.setTaintWrapper(easyWrapper);
			} catch (IOException e) {
				System.err.println("Could not initialize Taintwrapper:");
				e.printStackTrace();
			}
    		
    	}
    	return result;
    }
    
}