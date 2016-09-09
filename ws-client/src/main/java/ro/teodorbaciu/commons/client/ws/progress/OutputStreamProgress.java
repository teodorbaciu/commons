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

/**
 * Output stream that allows the notification of listeners about the amount of bytes written.
 * 
 * @author Teodor Baciu
 *
 */
public class OutputStreamProgress extends OutputStream {

	private final OutputStream outstream;

	private volatile long bytesWritten = 0;

	private final WriteListener writeListener;

	public OutputStreamProgress(OutputStream outstream, WriteListener writeListener) {
		this.outstream = outstream;
		this.writeListener = writeListener;
	}

	@Override
	public void write(int data) throws IOException {
		outstream.write(data);
		bytesWritten++;
		writeListener.registerWrite(bytesWritten);
	}

	@Override
	public void write(byte[] data) throws IOException {
		outstream.write(data);
		bytesWritten += data.length;
		writeListener.registerWrite(bytesWritten);
	}

	@Override
	public void write(byte[] data, int off, int len) throws IOException {
		outstream.write(data, off, len);
		bytesWritten += len;
		writeListener.registerWrite(bytesWritten);
	}

	@Override
	public void flush() throws IOException {
		outstream.flush();
	}

	@Override
	public void close() throws IOException {
		outstream.close();
	}
}
