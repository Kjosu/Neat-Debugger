package de.kjosu.neatdebug.old;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.neatdebug.old.GenomeRenderer;
import de.kjosu.neatdebug.old.RenderEngine;
import de.kjosu.neatdebug.old.RenderSettings;

public class NeatDebugger extends JFrame {

	private final Neat<?> neat;

	private final RenderSettings settings = new RenderSettings();
	private final GenomeRenderer genomeRenderer = new GenomeRenderer(settings);
	private final RenderEngine engine = new RenderEngine(genomeRenderer);

	public NeatDebugger(final Neat<?> neat) {
		super("Neat Debugger");

		this.neat = neat;

		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				engine.stop();
			}
		});

		add(engine);

		engine.start();
	}

	public void showGenome(Genome<?> g) {
		genomeRenderer.setGenome(g);
	}

	public void showFittest() {
		genomeRenderer.setGenome(neat.getFittestGenome());
	}
}
