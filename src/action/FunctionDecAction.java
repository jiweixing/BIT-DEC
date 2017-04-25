package action;

import java.util.HashMap;

import com.github.abel533.echarts.Label;
import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.series.Force;
import com.github.abel533.echarts.series.force.Link;
import com.github.abel533.echarts.series.force.Node;
import com.github.abel533.echarts.style.LinkStyle;
import com.github.abel533.echarts.style.NodeStyle;
import com.github.abel533.echarts.style.TextStyle;
import com.github.abel533.echarts.util.EnhancedOption;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import app.Activator;
import core.dissambler.AsmAdCode;
import core.dissambler.AsmFuncModel;
import core.dissambler.AsmStructAna;
import core.dissambler.AsmTextSectionStruct;
import test.test;
import utils.Constant;
import view.AdvancedCodeView;
import view.CFGView;
import view.ConsoleFactory;
import view.FuncsView;

public class FunctionDecAction extends Action implements IWorkbenchAction, Runnable {
	private IWorkbenchWindow workbenchWindow;

	public FunctionDecAction(IWorkbenchWindow window) {
		if (window == null) {
			throw new IllegalArgumentException();
		}
		this.workbenchWindow = window;
		//this.setText("Generate C Code");
		this.setText("Function Call Graph");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/c.jpg"));
		this.setToolTipText("Generate C Code");
	}
	
	@Override
	public void run() {
		if (workbenchWindow != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			FuncsView funcsView = (FuncsView)page.findView(Constant.VIEW_FUNCSVIEW);
			Shell shell = workbenchWindow.getShell();
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
					shell, new LabelProvider());
			dialog.setElements(funcsView.getList());
			/*
			 * ѡ��һ���������п������͸߼���������
			 */
			dialog.setTitle("Which function do you want to dec");
			if (dialog.open() != Window.OK) {
				return;
			}
			Object[] result = dialog.getResult();
			if(result[0]!=null){
				String funcName = (String)result[0];
				decAction(funcName,page);
			}
		}
	}
	/**
	 * ��ĳ���������п����������͸߼���������
	 * @param funcName
	 */
	//�˵����Ǵ���߷��� ��������ע����Ц����
	public static void decAction(String funcName,IWorkbenchPage workbenchPage){
		//		
		//new test().run();
		
		
		
		HashMap<String, AsmFuncModel> funcMap = AsmTextSectionStruct.textSectionModel.getFuncMap();
		AsmStructAna structAna = new AsmStructAna();
		try {
			AsmFuncModel funcModel = structAna.genCfg(funcMap.get(funcName));
			//AsmFuncModel funcModel = funcMap.get(funcName);
			//==============����������ͼ================//
			CFGView graphView = (CFGView)workbenchPage.findView(Constant.VIEW_CGF); 
			graphView.drawCFG(funcModel);
			//==============����߼�����================//
			AsmAdCode showHighCode = new AsmAdCode(); 
			String highcodeContent =  showHighCode.cfgAna(funcName);
			AdvancedCodeView adCodeView = (AdvancedCodeView) workbenchPage.findView(Constant.VIEW_ADVANCEDCODE);
			adCodeView.init();
			adCodeView.showContent(highcodeContent, 0);
		} catch (Exception e) {
			//MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "��ʾ", e.toString());
			MessageConsoleStream  printer =ConsoleFactory.getConsole().newMessageStream();
			printer.println("�޷���������"+funcName);
		}
	}

	@Override
	public void dispose() {
		workbenchWindow = null;
	}
}