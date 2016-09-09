/*
Copyright 2015 Teodor Baciu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
/* For more information see:
 * http://stackoverflow.com/questions/7057342/how-to-get-a-progress-bar-for-a-file-upload-with-apache-httpclient-4
 */

package ro.teodorbaciu.commons.client.ws.progress;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

/**
 * Allows the registration of a listener class for data write progress monitoring.
 * 
 * @author Teodor Baciu
 *
 */
public class MultipartEntityWithProgressMonitoring extends MultipartEntity {

	private OutputStreamProgress outstream;
	private WriteListener writeListener;

	public MultipartEntityWithProgressMonitoring(WriteListener writeListener) {
		super();
		this.writeListener = writeListener;
	}

	public MultipartEntityWithProgressMonitoring(HttpMultipartMode mode, WriteListener writeListener) {
		super(mode);
		this.writeListener = writeListener;
	}

	public MultipartEntityWithProgressMonitoring(HttpMultipartMode mode, String boundary, Charset charset, WriteListener writeListener) {
		super(mode, boundary, charset);
		this.writeListener = writeListener;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		this.outstream = new OutputStreamProgress(outstream, writeListener);
		super.writeTo(this.outstream);
	}

}
