package codeRewritePackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CodeRewrite {
	
	public CodeRewrite() {
		
	}
	
	public void codeRewrite(int start,boolean[] liner,String[] methodnames,String[] returner,String[] args,String filepathString) {
		File sourceFile = new File(filepathString);
		String targetString  = sourceFile.getAbsolutePath().replace(".java", "")+"_copy.java";
		File targetFile = new File(targetString);
		try {
			targetFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
            BufferedReader br = new BufferedReader(new FileReader(filepathString));

            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(targetString, true)));

            int lineNum = 1;//表示行号
            String lineString = null;
            boolean[] flag = {false,false,false,false};
            String flagStrings0="import java.io.File;";
            String flagStrings1="import java.io.FileOutputStream;";
            String flagStrings2="import java.io.IOException;";
            String flagStrings3="import java.io.OutputStreamWriter;";
            boolean Flagimport=true;
            while ((lineString = br.readLine()) != null) {
            	if(lineString.length()>6&&Flagimport)
            	{
            		String string =lineString.substring(0, 6);
            		//System.out.println(string);
            		if(string.equals("import")&& Flagimport)
            		{
            			if(lineString.equals(flagStrings0)) flag[0]=true;
            			else if(lineString.equals(flagStrings1)) flag[1]=true;
            			else if(lineString.equals(flagStrings2)) flag[2]=true;
            			else if(lineString.equals(flagStrings3)) flag[3]=true;
            		}else if(Flagimport){
            			Flagimport=false;
            			if(!flag[0])out.println(flagStrings0);
            			if(!flag[1])out.println(flagStrings1);
            			if(!flag[2])out.println(flagStrings2);
            			if(!flag[3])out.println(flagStrings3);
            		}
            	}
            	
            	out.println(lineString);  
            	
            	 if(lineNum==start) {
                 	String write_into_1="public static void write_into_1(String line,String methodname,String rtn,String filepathString)throws IOException {\r\n" + 
                 			"	\r\n" + 
                 			"	StringBuffer sBuffer = new StringBuffer();\r\n" + 
                 			"	StringBuffer pathBuffer = new StringBuffer(filepathString);\r\n" + 
                 			"	\r\n" + 
                 			"	String nameString = pathBuffer.substring(pathBuffer.lastIndexOf(\"\\\\\")+1, pathBuffer.lastIndexOf(\".java\"));\r\n" + 
                 			"	String dirString = pathBuffer.substring(0, pathBuffer.lastIndexOf(\".java\"));\r\n" + 
                 			"	String proString = dirString.substring(dirString.indexOf(\"\\\\\", dirString.lastIndexOf(\"\\\\\")-7)+1,dirString.lastIndexOf(\"\\\\\"));\r\n" + 
                 			"\r\n" + 

                 			"	File rtFile = new File(\"D:\\\\codeforces\\\\results\"+ File.separator +nameString+\"_\"+proString+\"_1_line \"+line+\"_\"+methodname+\"()_\"+System.getProperty(\"java.version\")+\".txt\");\r\n" + 
                 			"	FileOutputStream fos = new FileOutputStream(rtFile);\r\n" + 
                 			"	OutputStreamWriter osw = new OutputStreamWriter(fos, \"UTF-8\");\r\n" + 
                 			"	osw.write(rtn);\r\n" + 
                 			"	osw.flush();\r\n" + 
                 			"}\r\n" + 
                 			"";
                 	String write_into_2="public static void write_into_2(String line,String methodname,String args,String filepathString)throws IOException {\r\n" + 
                 			"	StringBuffer sBuffer = new StringBuffer();\r\n" + 
                 			"	StringBuffer pathBuffer = new StringBuffer(filepathString);\r\n" + 
                 			"	\r\n" + 
                 			"	String nameString = pathBuffer.substring(pathBuffer.lastIndexOf(\"\\\\\")+1, pathBuffer.lastIndexOf(\".java\"));\r\n" + 
                 			"	String dirString = pathBuffer.substring(0, pathBuffer.lastIndexOf(\".java\"));\r\n" + 
                 			"	String proString = dirString.substring(dirString.indexOf(\"\\\\\", dirString.lastIndexOf(\"\\\\\")-7)+1,dirString.lastIndexOf(\"\\\\\"));\r\n" + 
                 			"	\r\n" + 
                 			"	File rtFile = new File(\"D:\\\\codeforces\\\\results\"+ File.separator +\"input_\"+nameString+\"_\"+proString+\"_2_line \"+line+\"_\"+methodname+\"()_\"+System.getProperty(\"java.version\")+\".txt\");\r\n" + 
                 			"	FileOutputStream fos = new FileOutputStream(rtFile);\r\n" + 
                 			"	OutputStreamWriter osw = new OutputStreamWriter(fos, \"UTF-8\");\r\n" + 
                 			"	osw.write(args);\r\n" + 
                 			"	osw.flush();\r\n" + 
                 			"}\r\n" + 
                 			"";

                 	out.println(write_into_1);
                    out.println(write_into_2);
                 }
                //这里写入插桩函数
                if(lineNum<liner.length)
                if(liner[lineNum]) {
                	String pathtemp = filepathString.replace("\\", "\\\\");
                	args[lineNum] = args[lineNum].replace("\\", "\\\\");
                	String inter = "	try {\n"+
                	"	write_into_1(\""+lineNum+"\",\""+methodnames[lineNum]+"\",String.valueOf("+returner[lineNum]+"),\""+pathtemp+"\");\n"+
                	"	} catch (IOException e) {\n"+
                	"		e.printStackTrace();\n"+
                	"		System.out.println(\"write_into_1 error\");\n"+
                	"	}\n"+
                	"	try {\n"+
                	"	write_into_2(\""+lineNum+"\",\""+methodnames[lineNum]+"\",String.valueOf("+args[lineNum]+"),\""+pathtemp+"\");\n"+
                	"	} catch (IOException e) {\n"+
                	"		e.printStackTrace();\n"+
                	"		System.out.println(\"write_into_2 error\");\n"+
                	"	}\n";
                	out.println(inter);
                }
                lineNum++;
            }
            if (br != null) {
                br.close();
            }
            if (out != null) {
                out.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void main() {
	
	}

}
