# Neat-Debugger

Debugging Tool for JNSTINCT (https://github.com/Kjosu/JNSTINCT)

## Features
- Colorized Nodes and Enabled/Disabled/Gated connections
- Dragable Nodes
- Node/Connection information inspector (Double-Click node/connection)

## COMING SOON
- Choose genome from population
- Add empty genome to population
- Remove genome from population
- Save/Load genomes
- Interactable node/connection inspectors
- Manually change weights/values of nodes/connections
- Mutate per button click
- Single/Multi-step evolving per button click

## IDEAS
- Genome fitness tracking
- Genome gene size tracking
- Activate network with manual input
- Show step-by-step activation calculation

## Usage
```java
Neat<?> neat = ...

NeatDebugger debugger = new NeatDebugger(neat);
debugger.setVisible(true);

while (training) {
  neat.evolve();
  debugger.showFittest();
}
```
