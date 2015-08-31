package com.kareebo.contacts.base;

import com.kareebo.contacts.thrift.RegisterIdentityInput;

import java.util.Vector;

/**
 * {@link PlaintextSerializer} for {@link com.kareebo.contacts.thrift.RegisterIdentityInput}
 */
public class RegisterIdentityInputPlaintextSerializer implements PlaintextSerializer
{
	final private RegisterIdentityInput registerIdentityInput;

	public RegisterIdentityInputPlaintextSerializer(final RegisterIdentityInput registerIdentityInput)
	{
		this.registerIdentityInput=registerIdentityInput;
	}

	@Override
	public Vector<byte[]> serialize()
	{
		final Vector<byte[]> ret=new Vector<>(PublicKeysPlaintextSerializer.LENGTH+(registerIdentityInput.getUSet().size()+2)
			                                                                           *HashBufferPlaintextSerializer
				                                                                            .LENGTH+LongPlaintextSerializer
					                                                                                    .LENGTH+UserAgentPlaintextSerializer
					                                                                                      .LENGTH);
		ret.addAll(new PublicKeysPlaintextSerializer(registerIdentityInput.getPublicKeys()).serialize());
		ret.addAll(new HashBufferPlaintextSerializer(registerIdentityInput.getUA()).serialize());
		ret.addAll(new LongPlaintextSerializer(registerIdentityInput.getUserIdA()).serialize());
		ret.addAll(new SetHashBufferPlaintextSerializer(registerIdentityInput.getUSet()).serialize());
		ret.addAll(new HashBufferPlaintextSerializer(registerIdentityInput.getUJ()).serialize());
		ret.addAll(new UserAgentPlaintextSerializer(registerIdentityInput.getUserAgent()).serialize());
		return ret;
	}
}