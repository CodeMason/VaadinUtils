package au.com.vaadinutils.layout;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("rawtypes")
public class TimePicker extends HorizontalLayout implements Field<Date>
{
	protected static final String TIME_FORMAT = "hh:mm a";
	protected static final String TIME_FORMAT2 = "hh:mma";
	private static final long serialVersionUID = 1826417125815798837L;
	protected static final String EMPTY = "--:--";
	String headerColor = "#B2D7FF";
	protected TextField displayTime = new TextField();
	protected Calendar dateTime = Calendar.getInstance();
	boolean isSet = false;
	protected ChangedHandler changedHandler;
	private String title;
	protected TextField field;
	private Property<Date> datasource;
	// private boolean isRequired;
	private String requiredErrorMessage;
	private int tabIndex;
	private boolean isBuffered;
	protected Validator timeValidator;
	Set<Validator> validators = new LinkedHashSet<>();
	Set<ValueChangeListener> listeners = new LinkedHashSet<>();

	protected Button pickerButton;

	public TimePicker(String title)
	{
		setCaption(title);
		this.title = title;
		timeValidator = new Validator()
		{

			private static final long serialVersionUID = 6579163030027373837L;

			@Override
			public void validate(Object value) throws InvalidValueException
			{

				if (value == null || value.equals(EMPTY))
				{
					return;
				}
				parseDate((String) value);

			}

		};
		buildUI(title);

	}

	protected void buildUI(String title)
	{

		field = new TextField();
		field.setWidth("125");
		field.setImmediate(true);
		displayTime.setImmediate(true);

		displayTime.addValidator(timeValidator);
		field.addValidator(timeValidator);
		field.addValueChangeListener(new ValueChangeListener()
		{

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event)
			{
				try
				{
					final Date parsedDate = parseDate((String) event.getProperty().getValue());

					if (parsedDate != null)
					{
						dateTime.set(Calendar.HOUR_OF_DAY, parsedDate.getHours());
						dateTime.set(Calendar.MINUTE, parsedDate.getMinutes());
						isSet = true;
						setNewValue();
					}
					TimePicker.this.valueChange(event);
					field.setComponentError(null);
				}
				catch (final InvalidValueException e)
				{
					field.setComponentError(new ErrorMessage()
					{
						private static final long serialVersionUID = 1L;

						@Override
						public String getFormattedHtmlMessage()
						{
							return e.getMessage();
						}

						@Override
						public ErrorLevel getErrorLevel()
						{
							return ErrorLevel.ERROR;
						}
					});
				}
			}
		});

		this.title = title;

		CssLayout hl = new CssLayout();
		hl.addStyleName("v-component-group");
		hl.setWidth("100%");
		hl.setHeight("35");

		pickerButton = new Button();
		pickerButton.setIcon(FontAwesome.CLOCK_O);

		hl.addComponent(pickerButton);
		hl.addComponent(field);
		pickerButton.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				showPopupTimePicker();

			}
		});
		addComponent(hl);
	}

	@Override
	public void setReadOnly(boolean readOnly)
	{
		field.setReadOnly(readOnly);
		pickerButton.setEnabled(!readOnly);
		super.setReadOnly(readOnly);
	}

	protected Date parseDate(String value)
	{
		if (value == null || value.equals(EMPTY))
		{
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
		try
		{
			return sdf.parse(value);
		}
		catch (ParseException e)
		{
			sdf = new SimpleDateFormat(TIME_FORMAT2);
			try
			{
				return sdf.parse(value);
			}
			catch (ParseException e2)
			{
				throw new InvalidValueException("Time format is " + TIME_FORMAT);
			}
		}
	}

	@Override
	public void focus()
	{
		super.focus();
	}

	@SuppressWarnings("deprecation")
	private void showPopupTimePicker()
	{

		try
		{
			if (field.getValue() != null)
			{
				Date value = parseDate(field.getValue());
				if (value != null)
				{
					int hourNumber = value.getHours() % 12;
					if (hourNumber == 0)
					{
						hourNumber = 12;
					}

					displayTime.setValue(field.getValue());
				}
			}
		}
		catch (Exception e)
		{
			logger.error(e);
			clearValue();
		}

		final Window window = new Window(title);
		window.setModal(true);
		window.setResizable(false);
		// window.setWidth("550");
		// window.setHeight("220");
		window.setClosable(false);

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setStyleName(Reindeer.BUTTON_SMALL);

		displayTime.setWidth("100");

		displayTime.addValueChangeListener(new ValueChangeListener()
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event)
			{
				final Date parsedDate = parseDate((String) event.getProperty().getValue());
				if (parsedDate != null)
				{
					dateTime.set(Calendar.HOUR_OF_DAY, parsedDate.getHours());
					dateTime.set(Calendar.MINUTE, parsedDate.getMinutes());
					isSet = true;
					setNewValue();
				}
			}
		});

		VerticalLayout hourPanelLabelWrapper = new VerticalLayout();
		hourPanelLabelWrapper.setWidth("225");

		Label hourLabel = new Label("Hour");

		hourLabel.setWidth("230");
		HorizontalLayout innerHourLabelPanel = new HorizontalLayout();

		innerHourLabelPanel.addComponent(hourLabel);
		innerHourLabelPanel.setWidth("100");
		hourPanelLabelWrapper.addComponent(innerHourLabelPanel);

		VerticalLayout minuteLabelWrapper = new VerticalLayout();
		minuteLabelWrapper.setWidth("60");
		Label minuteLabel = new Label("Minute");

		minuteLabel.setWidth("45");
		VerticalLayout innerMinuteLabelPanel = new VerticalLayout();
		innerMinuteLabelPanel.addComponent(minuteLabel);
		innerMinuteLabelPanel.setWidth("45");

		minuteLabelWrapper.addComponent(innerMinuteLabelPanel);

		HorizontalLayout hourPanel = new HorizontalLayout();

		HorizontalLayout amPmPanel = new HorizontalLayout();

		VerticalLayout amPmButtonPanel = new VerticalLayout();
		amPmPanel.addComponent(amPmButtonPanel);
		addAmPmButtons(amPmButtonPanel);

		HorizontalLayout hourButtonPanel = new HorizontalLayout();
		hourPanel.addComponent(hourButtonPanel);
		addHourButtons(hourButtonPanel, 2, 6);

		HorizontalLayout minutePanel = new HorizontalLayout();
		HorizontalLayout minuteButtonPanel = new HorizontalLayout();
		minutePanel.addComponent(minuteButtonPanel);
		addMinuteButtons(minuteButtonPanel, 2, 4);

		HorizontalLayout amPmHourWrapper = new HorizontalLayout();
		amPmHourWrapper.addComponent(hourPanel);
		amPmHourWrapper.addComponent(amPmPanel);
		hourPanelLabelWrapper.addComponent(amPmHourWrapper);

		layout.addComponent(hourPanelLabelWrapper);

		minuteLabelWrapper.addComponent(minutePanel);
		layout.addComponent(minuteLabelWrapper);
		layout.setExpandRatio(minuteLabelWrapper, 1);

		HorizontalLayout okcancel = new HorizontalLayout();
		okcancel.setSizeFull();
		Button ok = new Button("OK");

		ok.setWidth("75");
		ok.addClickListener(new ClickListener()
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				field.setValue(displayTime.getValue());
				window.close();
			}
		});

		Button cancel = new Button("Cancel");
		cancel.setWidth("75");
		cancel.addClickListener(new ClickListener()
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				window.close();

			}
		});

		Button clear = new Button("Clear");
		clear.setWidth("75");
		clear.addClickListener(new ClickListener()
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				isSet = false;
				clearValue();
			}
		});

		okcancel.addComponent(displayTime);
		okcancel.addComponent(cancel);
		okcancel.addComponent(clear);
		okcancel.addComponent(ok);
		okcancel.setSpacing(true);
		okcancel.setExpandRatio(displayTime, 0.50f);

		VerticalLayout wrapper = new VerticalLayout();

		wrapper.addComponent(layout);
		wrapper.addComponent(okcancel);
		window.setContent(wrapper);
		wrapper.setMargin(true);
		wrapper.setSpacing(true);

		UI.getCurrent().addWindow(window);

	}

	protected void addMinuteButtons(HorizontalLayout minuteButtonPanel, int rows, int cols)
	{
		String[] numbers = new String[] { "00", "10", "15", "20", "30", "40", "45", "50" };
		for (int col = 0; col < cols; col++)
		{
			VerticalLayout rowsLayout = new VerticalLayout();
			for (int row = 0; row < rows; row++)
			{

				final NativeButton button = new NativeButton("" + numbers[row + (col * rows)]);
				rowsLayout.addComponent(button);

				button.setStyleName(Reindeer.BUTTON_SMALL);

				button.setWidth("30");

				button.addClickListener(new ClickListener()
				{

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event)
					{

						String title = button.getCaption();
						dateTime.set(Calendar.MINUTE, Integer.parseInt(title));
						isSet = true;
						setNewValue();

					}
				});

			}
			minuteButtonPanel.addComponent(rowsLayout);
		}
	}

	protected void addHourButtons(HorizontalLayout hourButtonPanel, int rows, int cols)
	{
		int[] numbers = new int[] { 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
		for (int col = 0; col < cols; col++)
		{
			VerticalLayout rowsLayout = new VerticalLayout();
			for (int row = 0; row < rows; row++)
			{
				final NativeButton button = new NativeButton("" + numbers[col + (row * cols)]);
				rowsLayout.addComponent(button);
				button.setStyleName(Reindeer.BUTTON_SMALL);
				button.setWidth("30");

				button.addClickListener(new ClickListener()
				{

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event)
					{
						int hourToSet = Integer.parseInt(button.getCaption());
						hourToSet %= 12;
						if (dateTime.get(Calendar.HOUR_OF_DAY) >= 12)
						{
							hourToSet += 12;
						}

						dateTime.set(Calendar.HOUR_OF_DAY, hourToSet);

						isSet = true;
						setNewValue();
					}
				});
			}
			hourButtonPanel.addComponent(rowsLayout);
		}
	}

	protected void addAmPmButtons(VerticalLayout amPmButtonPanel)
	{
		final NativeButton am = new NativeButton("AM");
		final NativeButton pm = new NativeButton("PM");
		amPmButtonPanel.addComponent(am);
		amPmButtonPanel.addComponent(pm);

		am.setStyleName(Reindeer.BUTTON_SMALL);
		am.setWidth("35");

		am.addClickListener(new ClickListener()
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				dateTime.set(Calendar.AM_PM, Calendar.AM);
				isSet = true;
				setNewValue();
			}
		});
		pm.setStyleName(Reindeer.BUTTON_SMALL);
		pm.setWidth("35");

		pm.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				dateTime.set(Calendar.AM_PM, Calendar.PM);
				isSet = true;
				setNewValue();
			}
		});
	}

	public void addChangedHandler(ChangedHandler pChangedHandler)
	{
		this.changedHandler = pChangedHandler;
	}

	public void clearValue()
	{
		dateTime.setTime(new Date());
		dateTime.set(Calendar.HOUR, 0);
		dateTime.set(Calendar.MINUTE, 0);
		dateTime.set(Calendar.SECOND, 0);
		dateTime.set(Calendar.MILLISECOND, 0);
		dateTime.set(Calendar.AM_PM, Calendar.AM);
		isSet = false;
		displayTime.setValue(EMPTY);
		internalSetReadonlyFieldValue(EMPTY);
	}

	protected void internalSetReadonlyFieldValue(String value)
	{
		boolean isRo = field.isReadOnly();
		field.setReadOnly(false);
		field.setValue(value);
		field.setReadOnly(isRo);
	}

	public final String getValueAsString()
	{
		if (isSet)
		{
			int hour = dateTime.get(Calendar.HOUR);
			if (hour == 0)
			{
				hour = 12;
			}
			final int minute = dateTime.get(Calendar.MINUTE);
			final String amPm = dateTime.get(Calendar.HOUR_OF_DAY) < 12 ? "AM" : "PM";
			DecimalFormat df = new DecimalFormat("00");
			return df.format(hour) + ":" + df.format(minute) + " " + amPm;
		}
		return null;
	}

	public void setValues(Date date)
	{
		if (date != null)
		{
			dateTime.setTime(date);
			isSet = true;
			displayTime.setValue(getValueAsString());
			internalSetReadonlyFieldValue(getValueAsString());
		}
		else
		{
			clearValue();
		}
	}

	protected void setNewValue()
	{
		displayTime.setValue(getValueAsString());
		internalSetReadonlyFieldValue(getValueAsString());
		if (changedHandler != null)
		{
			changedHandler.onChanged(getValueAsString());

		}
	}

	@Override
	public boolean isInvalidCommitted()
	{
		return false;
	}

	@Override
	public void setInvalidCommitted(boolean isCommitted)
	{
	}

	@Override
	public void commit() throws SourceException, InvalidValueException
	{

		datasource.setValue(getValue());
	}

	@Override
	public void discard() throws SourceException
	{
		setValues(datasource.getValue());
	}

	@Override
	public void setBuffered(boolean buffered)
	{
		this.isBuffered = buffered;
	}

	@Override
	public boolean isBuffered()
	{
		return isBuffered;
	}

	Logger logger = org.apache.logging.log4j.LogManager.getLogger();

	@Override
	public boolean isModified()
	{
		Date value = getValue();
		if (datasource == null)
		{
			return false;
		}
		Date dsValue = datasource.getValue();
		if (dsValue == null)
		{
			boolean ret = value != null;
			if (ret)
			{
				logger.info("Values {} and {}", dsValue, value);
			}
			return ret;
		}
		if (value == null)
		{
			logger.info("Values {} and {}", dsValue, value);
			return true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("hh:mma");
		String v1 = sdf.format(dsValue);
		String v2 = sdf.format(value);
		boolean ret = !v1.equals(v2);
		if (ret)
		{
			logger.info("Values {} and {}", v1, v2);
		}
		return ret;
	}

	@Override
	public void addValidator(Validator validator)
	{
		validators.add(validator);
	}

	@Override
	public void removeValidator(Validator validator)
	{
		validators.remove(validator);
	}

	@Override
	public void removeAllValidators()
	{
		validators.clear();
	}

	@Override
	public Collection<Validator> getValidators()
	{
		Collection<Validator> validators = new LinkedList<>();
		validators.add(timeValidator);
		validators.addAll(this.validators);
		return validators;
	}

	@Override
	public boolean isValid()
	{
		boolean valid = true;
		try
		{
			this.validate();
		}
		catch (Exception e)
		{
			valid = false;
		}
		return valid;
	}

	@Override
	public void validate() throws InvalidValueException
	{
		timeValidator.validate(getValueAsString());
		for (Validator validator : validators)
		{
			validator.validate(getValue());
		}

	}

	@Override
	public boolean isInvalidAllowed()
	{
		return false;
	}

	@Override
	public void setInvalidAllowed(boolean invalidValueAllowed) throws UnsupportedOperationException
	{
	}

	@Override
	public Date getValue()
	{
		Date value = null;
		if (isSet)
		{
			value = dateTime.getTime();
		}

		return value;
	}

	@Override
	public void setValue(Date newValue)
	{
		setValues(newValue);

	}

	@Override
	public Class<Date> getType()
	{

		return Date.class;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener listener)
	{
		addListener(listener);
	}

	@Override
	public void addListener(ValueChangeListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void removeValueChangeListener(ValueChangeListener listener)
	{
		removeListener(listener);
	}

	@Override
	public void removeListener(ValueChangeListener listener)
	{
		listeners.remove(listener);
	}

	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event)
	{
		for (ValueChangeListener listener : listeners)
		{
			listener.valueChange(event);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyDataSource(Property newDataSource)
	{
		clearValue();
		datasource = newDataSource;
		if (datasource != null && datasource.getValue() != null)
		{
			setValues(datasource.getValue());
		}

	}

	@Override
	public Property<Date> getPropertyDataSource()
	{
		return datasource;
	}

	@Override
	public int getTabIndex()
	{
		return tabIndex;
	}

	@Override
	public void setTabIndex(int tabIndex)
	{
		this.tabIndex = tabIndex;

	}

	@Override
	public boolean isRequired()
	{
		return isVisible();

	}

	@Override
	public void setRequired(boolean required)
	{
		// isRequired = required;

	}

	@Override
	public void setRequiredError(String requiredMessage)
	{
		requiredErrorMessage = requiredMessage;

	}

	@Override
	public String getRequiredError()
	{
		return requiredErrorMessage;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public void clear()
	{

	}

}
