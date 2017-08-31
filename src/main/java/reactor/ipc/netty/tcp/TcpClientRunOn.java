/*
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactor.ipc.netty.tcp;

import java.util.Objects;
import javax.annotation.Nullable;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.JdkSslContext;
import io.netty.handler.ssl.SslContext;
import reactor.ipc.netty.resources.LoopResources;

/**
 * @author Stephane Maldini
 */
final class TcpClientRunOn extends TcpClientOperator {

	final LoopResources loopResources;
	final boolean       preferNative;

	TcpClientRunOn(TcpClient client, LoopResources loopResources, boolean preferNative) {
		super(client);
		this.loopResources = Objects.requireNonNull(loopResources, "loopResources");
		this.preferNative = preferNative;
	}

	@Override
	public Bootstrap configure() {
		Bootstrap b = source.configure();

		configure(b, preferNative, loopResources, sslContext());

		return b;
	}

	static void configure(Bootstrap b,
			boolean preferNative,
			LoopResources resources,
			@Nullable SslContext sslContext) {
		boolean useNative = preferNative && !(sslContext instanceof JdkSslContext);
		EventLoopGroup elg = resources.onClient(useNative);

		b.group(elg)
		 .channel(resources.onChannel(elg));
	}
}