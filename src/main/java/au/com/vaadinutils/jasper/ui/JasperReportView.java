package au.com.vaadinutils.jasper.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;

/**
 * Base class for a view that provides a report filter selection area and a
 * report viewing area.
 */
public abstract class JasperReportView extends HorizontalLayout implements View, JasperReportProperties
{
	public static final String NAME = "ReportView";
	private static final long serialVersionUID = 1L;

	// static private final transient Logger logger = LogManager.getLogger();

	final JasperReportLayout report;

	protected JasperReportView()
	{
		report = new JasperReportLayout(this);
	}

	@Override
	public void enter(ViewChangeEvent event)
	{
		this.setSizeFull();
		report.initScreen(new MainReportSplitPanel());
		this.addComponent(report);

	}

}
