package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;

public class ClearService {
    private final DataAccess dao;
    public ClearService(DataAccess dao) {
        this.dao = dao;
    }
    public void deleteDatabase() throws DataAccessException {
        dao.Clear();
    }
}
