package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;

public class ClearService {
    private final MemoryDataAccess dao;
    public ClearService(MemoryDataAccess dao) {
        this.dao = dao;
    }
    public void deleteDatabase() throws DataAccessException {
        dao.Clear();
    }
}
