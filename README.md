# Neat-Debugger

Debugging Tool for JNSTINCT (https://github.com/Kjosu/JNSTINCT)

## Features
- Shows colorized Nodes and Enabled/Disabled/Gated connections
- You can drag Nodes around the canvas
- You can double click Nodes for more information

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
