/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.dromara.mica.mqtt.codec;


import java.util.Objects;

/**
 * 解析结果集
 *
 * @author L.cm
 */
public class DecoderResult {
	public static final DecoderResult SUCCESS = new DecoderResult();

	private final boolean success;
	private final Throwable cause;

	public DecoderResult() {
		this(true, null);
	}

	public DecoderResult(Throwable cause) {
		this(false, Objects.requireNonNull(cause, "cause is null"));
	}

	public DecoderResult(boolean success, Throwable cause) {
		this.success = success;
		this.cause = cause;
	}

	public static DecoderResult failure(Throwable cause) {
		return new DecoderResult(cause);
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isFailure() {
		return !success;
	}

	public Throwable getCause() {
		return cause;
	}
}
