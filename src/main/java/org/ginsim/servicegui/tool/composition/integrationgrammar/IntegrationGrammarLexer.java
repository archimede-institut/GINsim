// $ANTLR 3.5 /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g 2013-02-07 16:49:06
package org.ginsim.servicegui.tool.composition.integrationgrammar;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class IntegrationGrammarLexer extends Lexer {
	public static final int EOF = -1;
	public static final int T__8 = 8;
	public static final int T__9 = 9;
	public static final int T__10 = 10;
	public static final int AND = 4;
	public static final int ID = 5;
	public static final int NUMBER = 6;
	public static final int OR = 7;

	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public IntegrationGrammarLexer() {
	}

	public IntegrationGrammarLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public IntegrationGrammarLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override
	public String getGrammarFileName() {
		return "/Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g";
	}

	// $ANTLR start "T__8"
	public final void mT__8() throws RecognitionException {
		try {
			int _type = T__8;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:2:6:
			// ( '(' )
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:2:8:
			// '('
			{
				match('(');
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__8"

	// $ANTLR start "T__9"
	public final void mT__9() throws RecognitionException {
		try {
			int _type = T__9;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:3:6:
			// ( ')' )
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:3:8:
			// ')'
			{
				match(')');
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__9"

	// $ANTLR start "T__10"
	public final void mT__10() throws RecognitionException {
		try {
			int _type = T__10;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:4:7:
			// ( ',' )
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:4:9:
			// ','
			{
				match(',');
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__10"

	// $ANTLR start "NUMBER"
	public final void mNUMBER() throws RecognitionException {
		try {
			int _type = NUMBER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:34:9:
			// ( ( '0' .. '9' )+ )
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:34:11:
			// ( '0' .. '9' )+
			{
				// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:34:11:
				// ( '0' .. '9' )+
				int cnt1 = 0;
				loop1: while (true) {
					int alt1 = 2;
					int LA1_0 = input.LA(1);
					if (((LA1_0 >= '0' && LA1_0 <= '9'))) {
						alt1 = 1;
					}

					switch (alt1) {
					case 1:
					// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:
					{
						if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
							input.consume();
						} else {
							MismatchedSetException mse = new MismatchedSetException(
									null, input);
							recover(mse);
							throw mse;
						}
					}
						break;

					default:
						if (cnt1 >= 1)
							break loop1;
						EarlyExitException eee = new EarlyExitException(1,
								input);
						throw eee;
					}
					cnt1++;
				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "NUMBER"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:35:4:
			// ( ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )+ )
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:35:6:
			// ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )+
			{
				// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:35:6:
				// ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )+
				int cnt2 = 0;
				loop2: while (true) {
					int alt2 = 2;
					int LA2_0 = input.LA(1);
					if ((LA2_0 == '-' || (LA2_0 >= '0' && LA2_0 <= '9')
							|| (LA2_0 >= 'A' && LA2_0 <= 'Z') || LA2_0 == '_' || (LA2_0 >= 'a' && LA2_0 <= 'z'))) {
						alt2 = 1;
					}

					switch (alt2) {
					case 1:
					// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:
					{
						if (input.LA(1) == '-'
								|| (input.LA(1) >= '0' && input.LA(1) <= '9')
								|| (input.LA(1) >= 'A' && input.LA(1) <= 'Z')
								|| input.LA(1) == '_'
								|| (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
							input.consume();
						} else {
							MismatchedSetException mse = new MismatchedSetException(
									null, input);
							recover(mse);
							throw mse;
						}
					}
						break;

					default:
						if (cnt2 >= 1)
							break loop2;
						EarlyExitException eee = new EarlyExitException(2,
								input);
						throw eee;
					}
					cnt2++;
				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "ID"

	// $ANTLR start "OR"
	public final void mOR() throws RecognitionException {
		try {
			int _type = OR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:36:4:
			// ( '|' )
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:36:6:
			// '|'
			{
				match('|');
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "OR"

	// $ANTLR start "AND"
	public final void mAND() throws RecognitionException {
		try {
			int _type = AND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:37:5:
			// ( '&' )
			// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:37:7:
			// '&'
			{
				match('&');
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "AND"

	@Override
	public void mTokens() throws RecognitionException {
		// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:1:8:
		// ( T__8 | T__9 | T__10 | NUMBER | ID | OR | AND )
		int alt3 = 7;
		alt3 = dfa3.predict(input);
		switch (alt3) {
		case 1:
		// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:1:10:
		// T__8
		{
			mT__8();

		}
			break;
		case 2:
		// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:1:15:
		// T__9
		{
			mT__9();

		}
			break;
		case 3:
		// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:1:20:
		// T__10
		{
			mT__10();

		}
			break;
		case 4:
		// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:1:26:
		// NUMBER
		{
			mNUMBER();

		}
			break;
		case 5:
		// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:1:33:
		// ID
		{
			mID();

		}
			break;
		case 6:
		// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:1:36:
		// OR
		{
			mOR();

		}
			break;
		case 7:
		// /Users/nuno/projects/nmd/ginsim-dev/src/main/java/org/ginsim/servicegui/tool/composition/IntegrationGrammar.g:1:39:
		// AND
		{
			mAND();

		}
			break;

		}
	}

	protected DFA3 dfa3 = new DFA3(this);
	static final String DFA3_eotS = "\4\uffff\1\10\4\uffff";
	static final String DFA3_eofS = "\11\uffff";
	static final String DFA3_minS = "\1\46\3\uffff\1\55\4\uffff";
	static final String DFA3_maxS = "\1\174\3\uffff\1\172\4\uffff";
	static final String DFA3_acceptS = "\1\uffff\1\1\1\2\1\3\1\uffff\1\5\1\6\1\7\1\4";
	static final String DFA3_specialS = "\11\uffff}>";
	static final String[] DFA3_transitionS = {
			"\1\7\1\uffff\1\1\1\2\2\uffff\1\3\1\5\2\uffff\12\4\7\uffff\32\5\4\uffff"
					+ "\1\5\1\uffff\32\5\1\uffff\1\6", "", "", "",
			"\1\5\2\uffff\12\4\7\uffff\32\5\4\uffff\1\5\1\uffff\32\5", "", "",
			"", "" };

	static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
	static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
	static final char[] DFA3_min = DFA
			.unpackEncodedStringToUnsignedChars(DFA3_minS);
	static final char[] DFA3_max = DFA
			.unpackEncodedStringToUnsignedChars(DFA3_maxS);
	static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
	static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
	static final short[][] DFA3_transition;

	static {
		int numStates = DFA3_transitionS.length;
		DFA3_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
		}
	}

	protected class DFA3 extends DFA {

		public DFA3(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 3;
			this.eot = DFA3_eot;
			this.eof = DFA3_eof;
			this.min = DFA3_min;
			this.max = DFA3_max;
			this.accept = DFA3_accept;
			this.special = DFA3_special;
			this.transition = DFA3_transition;
		}

		@Override
		public String getDescription() {
			return "1:1: Tokens : ( T__8 | T__9 | T__10 | NUMBER | ID | OR | AND );";
		}
	}

}
