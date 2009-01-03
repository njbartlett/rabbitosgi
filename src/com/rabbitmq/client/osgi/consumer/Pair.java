package com.rabbitmq.client.osgi.consumer;

public class Pair<A, B> {
	private final A fst;
	private final B snd;

	public Pair(A fst, B snd) {
		this.fst = fst;
		this.snd = snd;
	}

	public A getFst() {
		return fst;
	}

	public B getSnd() {
		return snd;
	}

}
