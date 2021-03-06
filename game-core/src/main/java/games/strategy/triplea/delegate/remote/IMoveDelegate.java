package games.strategy.triplea.delegate.remote;

import java.util.Collection;
import java.util.Map;

import games.strategy.engine.data.PlayerId;
import games.strategy.engine.data.Route;
import games.strategy.engine.data.Territory;
import games.strategy.engine.data.Unit;
import games.strategy.triplea.delegate.UndoableMove;

/**
 * Remote interface for MoveDelegate.
 */
public interface IMoveDelegate extends IAbstractMoveDelegate<UndoableMove>, IAbstractForumPosterDelegate {
  /**
   * Moves the specified units along the specified route.
   *
   * @param units - the units to move.
   * @param route - the route to move along
   * @param transportsThatCanBeLoaded - transports that can be loaded while moving, must be non null
   * @return an error message if the move can't be made, null otherwise
   */
  String move(Collection<Unit> units, Route route, Collection<Unit> transportsThatCanBeLoaded);

  /**
   * Moves the specified units along the specified route accounting for dependents.
   *
   * @param units - the units to move.
   * @param route - the route to move along
   * @param transportsThatCanBeLoaded - transports that can be loaded while moving, must be non null
   * @param newDependents - units that will be made into new dependents if this move is successful, must be non null
   * @return an error message if the move can't be made, null otherwise
   */
  String move(Collection<Unit> units, Route route, Collection<Unit> transportsThatCanBeLoaded,
      Map<Unit, Collection<Unit>> newDependents);

  /**
   * equivalent to move(units, route, Collections.EMPTY_LIST)
   *
   * @param units - the units to move
   * @param route - the route to move along
   * @return an error message if the move cant be made, null otherwise
   */
  String move(Collection<Unit> units, Route route);

  /**
   * Get what air units must move before the end of the players turn.
   *
   * @param player referring player ID
   * @return a list of territories with air units that must move of player ID
   */
  Collection<Territory> getTerritoriesWhereAirCantLand(PlayerId player);

  Collection<Territory> getTerritoriesWhereAirCantLand();

  /**
   * Get what units must have combat ability.
   *
   * @return a list of Territories with units that can't fight
   */
  Collection<Territory> getTerritoriesWhereUnitsCantFight();
}
