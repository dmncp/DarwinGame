package agh.cs.evolution_generator.Maps;

public enum MapDirection {
    NORTH,
    SOUTH,
    WEST,
    EAST,
    NORTH_EAST,
    NORTH_WEST,
    SOUTH_WEST,
    SOUTH_EAST,
    ERROR;

    public String toString(){
        switch (this){
            case EAST:
                return "Wschod";
            case NORTH_EAST:
                return "Polnocny Wschod";
            case WEST:
                return "Zachod";
            case NORTH_WEST:
                return "Polnocny Zachod";
            case SOUTH:
                return "Poludnie";
            case SOUTH_EAST:
                return "Poludniowy Wschod";
            case SOUTH_WEST:
                return "Poludniowy Zachod";
            case NORTH:
                return "Polnoc";
            default:
                return "Error";
        }
    }

    public MapDirection next(){ //turn right
        switch (this){
            case NORTH:
                return NORTH_EAST;
            case NORTH_EAST:
                return EAST;
            case EAST:
                return SOUTH_EAST;
            case SOUTH_EAST:
                return SOUTH;
            case SOUTH:
                return SOUTH_WEST;
            case SOUTH_WEST:
                return WEST;
            case WEST:
                return NORTH_WEST;
            case NORTH_WEST:
                return NORTH;
            default:
                return ERROR;
        }
    }

    public MapDirection previous(){ //turn left
        switch (this){
            case EAST:
                return NORTH_EAST;
            case NORTH_EAST:
                return NORTH;
            case NORTH:
                return NORTH_WEST;
            case NORTH_WEST:
                return WEST;
            case WEST:
                return SOUTH_WEST;
            case SOUTH_WEST:
                return SOUTH;
            case SOUTH:
                return SOUTH_EAST;
            case SOUTH_EAST:
                return EAST;
            default:
                return ERROR;
        }
    }

    public Vector2d toUnitVector(){
        switch (this){
            case EAST:
                return new Vector2d(1, 0);
            case NORTH_EAST:
                return new Vector2d(1,1);
            case NORTH:
                return new Vector2d(0, 1);
            case NORTH_WEST:
                return new Vector2d(-1, 1);
            case WEST:
                return new Vector2d(-1, 0);
            case SOUTH_WEST:
                return new Vector2d(-1, -1);
            case SOUTH:
                return new Vector2d(0, -1);
            case SOUTH_EAST:
                return new Vector2d(1, -1);
            default:
                return new Vector2d(0, 0); // as ERROR
        }
    }

    public static MapDirection randomStartingDir(){
        int a = (int) (Math.random() * 8);
        return switch (a) {
            case 0 -> NORTH;
            case 1 -> SOUTH;
            case 2 -> WEST;
            case 3 -> EAST;
            case 4 -> NORTH_EAST;
            case 5 -> NORTH_WEST;
            case 6 -> SOUTH_WEST;
            case 7 -> SOUTH_EAST;
            default -> ERROR;
        };
    }

}
