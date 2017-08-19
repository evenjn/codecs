/**
 *
 * Copyright 2016 Marco Trevisan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.github.evenjn.plaintext;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.function.Consumer;

import org.github.evenjn.yarn.RookFunction;
import org.github.evenjn.yarn.Rook;

@Deprecated
public class LineWriterBlueprint {

	public RookFunction<OutputStream, Consumer<String>> build( ) {
		final Charset local_cs = cs;
		final String local_delimiter = delimiter;
		final boolean local_force_flush = force_flush;
		RookFunction<OutputStream, Consumer<String>> result =
				new RookFunction<OutputStream, Consumer<String>>( ) {

					@Override
					public Consumer<String> get( Rook rook, OutputStream output_stream ) {
						return PlainText.write( rook, output_stream, local_cs,
								local_delimiter, local_force_flush );
					}
				};
		return result;
	}

	private Charset cs = Charset.forName( "UTF-8" );

	private String delimiter = "\n";

	private boolean force_flush = true;

	public LineWriterBlueprint setCharset( Charset cs ) {
		this.cs = cs;
		return this;
	}

	public LineWriterBlueprint setDelimiter( String delimiter ) {
		this.delimiter = delimiter;
		return this;
	}

	public LineWriterBlueprint setForcedFlush( boolean force_flush ) {
		this.force_flush = force_flush;
		return this;
	}

}
