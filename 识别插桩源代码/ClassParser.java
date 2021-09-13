package com.javaparser_test.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import codeRewritePackage.CodeRewrite;

public class ClassParser {
	
	// 递归函数
	public void getpath(File f)
	{
		
		String filepathString=f.getAbsolutePath().toString();
		if((filepathString.indexOf(".java")!=-1)&&(filepathString.indexOf("_copy.java")==-1))
		{
			parsecode(filepathString);
		}
		
		if (f.isDirectory())
		{
			File[] files = f.listFiles();
			for (File temp : files)
			{
				getpath(temp);
			}
		}
	}

	void parsecode(String filepathString)
	{
		try {
			BufferedReader in = new BufferedReader(new FileReader(filepathString));
			StringBuilder sb = new StringBuilder();
			String s = "";
			int maxline=0;//用以标记哪些行是函数
			while ((s =in.readLine()) != null) {
				sb.append(s + "\n");
				maxline++;
			}
			in.close();
			String javacode = sb.toString();
			
			//初始化标记数组，Boolean型初值是false
			final boolean[] func = new boolean[maxline];
			final boolean[] assigns = new boolean[maxline];
			final String[] returns = new String[maxline];
			final String[] methodnames = new String[maxline];
			final String[] argums = new String[maxline];
			final int[] start = new int[2];
			
			CompilationUnit result = JavaParser.parse(javacode);
			VoidVisitorAdapter<Object>adapter=new VoidVisitorAdapter<Object>()
			{
   
				public void visit(MethodCallExpr exp,Object arg)
				{
					super.visit(exp, arg);
					methodnames[exp.getRange().get().begin.line]= exp.getName().getIdentifier();
					if(exp.getArguments().size()==0)
						argums[exp.getRange().get().begin.line] = "\"NULL\"";
					else
						argums[exp.getRange().get().begin.line] =exp.getArguments().get(0).toString();
					func[exp.getRange().get().begin.line] = true;//标记该行为函数
				}
				
				public void visit(AssignExpr exp, Object arg) {
					super.visit(exp, arg);
					if(func[exp.getRange().get().begin.line]) {
						returns[exp.getRange().get().begin.line] = exp.getTarget().toString();
						assigns[exp.getRange().get().begin.line] = true;
					}
				}
				
				public void visit(ClassOrInterfaceDeclaration exp, Object arg) {
					super.visit(exp, arg);
					start[0] = exp.getRange().get().begin.line;
				}

			};
			adapter.visit(result, null);
			
			new CodeRewrite().codeRewrite(start[0],assigns,methodnames,returns,argums,filepathString);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
    public static void main(String[] args) throws IOException {
    	// 创建一个文件
    	File f = new File("D:\\codeforces");
    	f.createNewFile();
    	new ClassParser().getpath(f);
    }

}

