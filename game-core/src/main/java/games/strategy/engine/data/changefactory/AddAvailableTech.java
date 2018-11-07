package games.strategy.engine.data.changefactory;

import static com.google.common.base.Preconditions.checkNotNull;

import games.strategy.engine.data.Change;
import games.strategy.engine.data.GameData;
import games.strategy.engine.data.PlayerId;
import games.strategy.engine.data.TechnologyFrontier;
import games.strategy.triplea.delegate.TechAdvance;

class AddAvailableTech extends Change {
  private static final long serialVersionUID = 5664428883866434959L;

  private final TechAdvance tech;
  private final TechnologyFrontier frontier;
  private final PlayerId player;

  public AddAvailableTech(final TechnologyFrontier front, final TechAdvance tech, final PlayerId player) {
    checkNotNull(front);
    checkNotNull(tech);

    this.tech = tech;
    frontier = front;
    this.player = player;
  }

  @Override
  public void perform(final GameData data) {
    final TechnologyFrontier front = player.getTechnologyFrontierList().getTechnologyFrontier(frontier.getName());
    front.addAdvance(tech);
  }

  @Override
  public Change invert() {
    return new RemoveAvailableTech(frontier, tech, player);
  }
}
