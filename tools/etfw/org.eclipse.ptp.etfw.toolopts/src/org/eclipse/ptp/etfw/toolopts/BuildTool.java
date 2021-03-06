package org.eclipse.ptp.etfw.toolopts;

import java.util.ArrayList;
import java.util.Arrays;

public class BuildTool extends ExternalTool {

	
	/**
	 * If true the compiler command is entirely replaced rather than prepended
	 */
	public boolean replaceCompiler=false;
	


	
	protected ToolApp ccCompiler=null;
	protected ToolApp cxxCompiler=null;
	protected ToolApp f90Compiler=null;
	protected ToolApp allCompilers=null;
	protected ToolApp upcCompiler=null;
	
	public ArrayList<ToolPane> getAllCompilerPanes(){
		
		ArrayList<ToolPane> allPanes=new ArrayList<ToolPane>();
		
		if(ccCompiler!=null&&ccCompiler.toolPanes!=null){
			allPanes.addAll(Arrays.asList(ccCompiler.toolPanes));
		}
		if(cxxCompiler!=null&&cxxCompiler.toolPanes!=null){
			allPanes.addAll(Arrays.asList(cxxCompiler.toolPanes));
		}
		if(f90Compiler!=null&&f90Compiler.toolPanes!=null){
			allPanes.addAll(Arrays.asList(f90Compiler.toolPanes));
		}
		if(upcCompiler!=null&&upcCompiler.toolPanes!=null){
			allPanes.addAll(Arrays.asList(upcCompiler.toolPanes));
		}
		if(allCompilers!=null&&allCompilers.toolPanes!=null){
			allPanes.addAll(Arrays.asList(allCompilers.toolPanes));
		}
		
		return allPanes;
	}
	
	public ToolApp getCcCompiler(){
		if(ccCompiler!=null)
			return ccCompiler;
		return allCompilers;
	}
	public ToolApp getCxxCompiler(){
		if(cxxCompiler!=null)
			return cxxCompiler;
		return allCompilers;
	}
	public ToolApp getF90Compiler(){
		if(f90Compiler!=null)
			return f90Compiler;
		return allCompilers;
	}
	public ToolApp getUPCCompiler(){
		if(upcCompiler!=null)
			return upcCompiler;
		return allCompilers;
	}
	public ToolApp getGlobalCompiler(){
		return allCompilers;
	}
	

}
