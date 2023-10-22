package gr.petros.ui;

import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DownloadLink extends ResourceLink<Void> {
    public DownloadLink(String id, IModel<byte[]> model) {
        super(id, new ResourceStreamResource() {
            ByteArrayInputStream bis;

            @Override
            protected IResourceStream getResourceStream(Attributes attributes) {
                return new AbstractResourceStream() {
                    @Override
                    public InputStream getInputStream() throws ResourceStreamNotFoundException {
                        bis = new ByteArrayInputStream(model.getObject());
                        return bis;
                    }

                    @Override
                    public void close() throws IOException {
                        if (bis != null) {
                            bis.close();
                            bis = null;
                        }
                    }
                };
            }
        });
    }

}
