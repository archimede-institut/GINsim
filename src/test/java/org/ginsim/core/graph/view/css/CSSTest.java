package org.ginsim.core.graph.view.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Color;

import org.ginsim.core.graph.view.NodeShape;
import org.junit.BeforeClass;
import org.junit.Test;

public class CSSTest {
	
	@BeforeClass
	public static void beforeAllTests(){
		Selector.registerSelector(AllSelector.IDENTIFIER, AllSelector.class);
	}
		
	
	@Test
	public void singleStyleTest() {
		CascadingStyleSheet css = new CascadingStyleSheet();
		
		css.cascade.add(new StyleSheet(AllSelector.IDENTIFIER, AllSelector.CAT_NODES, new CSSNodeStyle(Color.red, Color.blue, Color.green, null, NodeShape.RECTANGLE)));
		

		CascadingStyleSheet css2 = null;
		try {
			css2 = CascadingStyleSheet.newFromText("all.nodes { \t  background: #ff0000;\nforeground: #0000ff;text-color: #00ff00;shape: rectangle;}");
		} catch (CSSSyntaxException e) {
			e.printStackTrace();
			fail();
		}
		assertEquals("Manually constructed and parsed css doesn't match", css.toString(), css2.toString());

	}
	
}
