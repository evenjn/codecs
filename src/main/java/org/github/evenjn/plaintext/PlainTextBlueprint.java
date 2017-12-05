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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.github.evenjn.lang.Rook;
import org.github.evenjn.yarn.Cursor;
import org.github.evenjn.yarn.CursorRingMap;
import org.github.evenjn.yarn.EndOfCursorException;
import org.github.evenjn.yarn.RingFunction;

public class PlainTextBlueprint {

	public static final PlainTextBlueprint nu( ) {
		return new PlainTextBlueprint( );
	}

	public Supplier<RingFunction<OutputStream, Consumer<String>>> writer( ) {
		PlainTextBlueprint klone = klone( this );
		return new Supplier<RingFunction<OutputStream, Consumer<String>>>( ) {

			@Override
			public RingFunction<OutputStream, Consumer<String>> get( ) {
				return new RingFunction<OutputStream, Consumer<String>>( ) {

					@Override
					public Consumer<String> apply( Rook rook,
							OutputStream output_stream ) {
						return write( rook, output_stream, klone.cs,
								klone.delimiter, klone.force_flush );
					}
				};
			}
		};
	}

	private static Consumer<String> write(
			Rook rook,
			OutputStream os,
			Charset cs,
			String delimiter,
			boolean force_flush ) {
		CharsetEncoder encoder = cs.newEncoder( );
		Writer writer = rook.hook( new OutputStreamWriter( os, encoder ) );
		BufferedWriter buffered_writer =
				rook.hook( new BufferedWriter( writer ) );
		return new Consumer<String>( ) {

			@Override
			public void accept( String t ) {
				try {
					buffered_writer.append( t );
					if ( delimiter != null ) {
						buffered_writer.append( delimiter );
					}
					if ( force_flush ) {
						buffered_writer.flush( );
					}
				}
				catch ( IOException e ) {
					throw new RuntimeException( e );
				}
			}
		};
	}

	public Supplier<CursorRingMap<InputStream, String>> reader( ) {
		PlainTextBlueprint klone = klone( this );
		return new Supplier<CursorRingMap<InputStream, String>>( ) {

			@Override
			public CursorRingMap<InputStream, String> get( ) {
				return new CursorRingMap<InputStream, String>( ) {

					@Override
					public Cursor<String> get( Rook rook, InputStream input ) {
						return read( rook, input, klone.cs, klone.delimiter_pattern );
					}
				};
			}
		};

	}

	private static Cursor<String> read(
			Rook rook,
			InputStream input,
			Charset cs,
			Pattern delimiter ) {
		Reader reader = rook.hook( new InputStreamReader( input, cs ) );
		BufferedReader buffered_reader =
				rook.hook( new BufferedReader( reader ) );

		Scanner scanner = rook.hook( new Scanner( buffered_reader ) );
		scanner.useDelimiter( delimiter );
		return new Cursor<String>( ) {

			@Override
			public String next( )
					throws EndOfCursorException {
				for ( ;; ) {
					try {
						boolean hasNext = scanner.hasNext( );
						if ( scanner.ioException( ) != null ) {
							throw scanner.ioException( );
						}
						if ( hasNext ) {

							String next = scanner.next( );
							if ( scanner.ioException( ) != null ) {
								throw scanner.ioException( );
							}
							return next;
						}
						throw EndOfCursorException.neo( );
					}
					catch ( IOException e ) {
						throw new RuntimeException( e );
					}
				}
			}
		};
	}

	private Pattern delimiter_pattern = Pattern.compile( "[\\x0D]?[\\x0A]" );

	private Charset cs = Charset.forName( "UTF-8" );

	private String delimiter = "\n";

	private boolean force_flush = true;

	public PlainTextBlueprint setCharset( Charset cs ) {
		this.cs = cs;
		return this;
	}

	public PlainTextBlueprint setDelimiter(
			String delimiter,
			Pattern delimiter_pattern ) {
		this.delimiter = delimiter;
		this.delimiter_pattern = delimiter_pattern;
		return this;
	}

	public PlainTextBlueprint setForcedFlush( boolean force_flush ) {
		this.force_flush = force_flush;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <K extends PlainTextBlueprint> K klone( K kloneable )
			throws IllegalArgumentException {
		if ( this != kloneable ) {
			throw new IllegalArgumentException( );
		}
		PlainTextBlueprint klone = new PlainTextBlueprint( );
		klone.cs = cs;
		klone.delimiter = delimiter;
		klone.delimiter_pattern = delimiter_pattern;
		klone.force_flush = force_flush;
		return (K) klone;
	}

}
