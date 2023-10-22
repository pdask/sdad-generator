package gr.petros.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.IBootstrapButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.File;
import java.time.Duration;

public class BootstrapDownloadLink extends DownloadLink implements IBootstrapButton<BootstrapDownloadLink> {

    private final Icon icon;
    private final Component label;
    private final Component splitter;
    private final ButtonBehavior buttonBehavior;
    /**
     * To use the splitter or not (true by default).
     */
    private boolean useSplitter = true;

    public BootstrapDownloadLink(String id, IModel<File> fileModel, IModel<String> fileNameModel, Buttons.Type type) {
        super(id, fileModel, fileNameModel);
        setDeleteAfterDownload(true);
        setCacheDuration(Duration.ZERO);

        add(buttonBehavior = new ButtonBehavior(type, Buttons.Size.Medium));

        add(icon = newIcon("icon"));
        add(splitter = newSplitter("splitter"));
        add(label = newLabel("label"));
    }


    /**
     * creates a new icon component
     *
     * @param markupId the component id of the icon
     * @return new icon component
     */
    protected Icon newIcon(final String markupId) {
        return new Icon(markupId, (IconType) null);
    }

    /**
     * creates a new label component
     *
     * @param markupId the component id of the label
     * @return new label component
     */
    protected Component newLabel(final String markupId) {
        return new Label(markupId, new Model<>("")).setRenderBodyOnly(true);
    }

    /**
     * creates a new splitter component. The splitter is visible only
     * if icon is visible and useSplitter is true.
     *
     * @param markupId the component id of the splitter
     * @return new splitter component
     */
    protected Component newSplitter(final String markupId) {
        return new WebMarkupContainer(markupId).setRenderBodyOnly(true).setEscapeModelStrings(false).setVisible(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final IMarkupSourcingStrategy newMarkupSourcingStrategy() {
        return new PanelMarkupSourcingStrategy(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        if (useSplitter) {
            splitter.setVisible(icon.hasIconType() && StringUtils.isNotEmpty(label.getDefaultModelObjectAsString()));
        }
    }

    public BootstrapDownloadLink setLabel(IModel<?> label) {
        this.label.setDefaultModel(label);
        return this;
    }

    /**
     * sets the button's icon which will be rendered in front of the label.
     *
     * @param iconType the new button icon type
     * @return reference to the current instance
     */
    public BootstrapDownloadLink setIconType(IconType iconType) {
        icon.setType(iconType);
        return this;
    }

    /**
     * sets the size of the button
     *
     * @param size The button size
     * @return this instance for chaining
     */
    public BootstrapDownloadLink setSize(Buttons.Size size) {
        buttonBehavior.setSize(size);
        return this;
    }

    /**
     * Sets the type of the button
     *
     * @param type The type of the button
     * @return this instance for chaining
     */
    public BootstrapDownloadLink setType(Buttons.Type type) {
        this.buttonBehavior.setType(type);
        return this;
    }

    /**
     * Sets whether this button should display inline or block
     *
     * @param block <code>true</code>, for block mode
     * @return this instance for chaining
     */
    public BootstrapDownloadLink setBlock(boolean block) {
        this.buttonBehavior.setBlock(block);
        return this;
    }

    /**
     * @param value whether to use splitter between the icon and the label or not
     * @return this instance for chaining
     */
    public BootstrapDownloadLink useSplitter(boolean value) {
        this.useSplitter = value;
        return this;
    }
}
