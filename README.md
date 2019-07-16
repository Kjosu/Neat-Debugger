# Neat-Debugger

Debugging tool for JNSTINCT (https://github.com/Kjosu/JNSTINCT)

<img src="./WIP.png" width="600" height="450">

## Features
- Add empty genome to population
- Remove genomes from population
- Save/load genomes
- Select and crossover two genomes
- Colorized nodes and enabled/disabled/gated connections
- Choose genome from population
- Interactable node/connection inspectors
- Manually change weights/values of nodes/connections
- Dragable nodes
- Node/connection inspector (double-click node/connection)

## COMING SOON
- Mutate per button click
- Single/multi-step evolving per button click

## IDEAS
- Genome fitness tracking
- Genome gene size tracking
- Activate network with manual input
- Show step-by-step activation calculation

## Usage
```java
class UsageExample extends Application {
  
  private NeatDebugger debugger;
  
  @Override
  public void start(Stage stage) throw Exception {
    Neat<T> neat = ...;
    
    debugger = new NeatDebugger(stage);
    debugger.setNeat(neat);
    
    stage.show();
  }
  
  @Override
  public void stop() {
    debugger.getController().dispose();
  }
}

```
