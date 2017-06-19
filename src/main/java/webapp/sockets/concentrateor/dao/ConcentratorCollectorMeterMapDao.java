package webapp.sockets.concentrateor.dao;


import org.apache.log4j.Logger;
import webapp.db.ConnectionPool;
import webapp.db.ConnectionPoolImpl;
import webapp.dbcp.DbcpProvider;
import webapp.sockets.concentrateor.dao.vo.ConcentratorCollectorMeterMapVo;
import webapp.sockets.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class ConcentratorCollectorMeterMapDao {
    private static Logger log = Logger.getLogger(ConcentratorCollectorMeterMapDao.class);
//    ConnectionPool pool = new ConnectionPoolImpl();

    /**
     * 查询表Id对应的集中器ID
     *
     * @param meterId
     * @return
     */
    public ArrayList<ConcentratorCollectorMeterMapVo> queryMapByMeterId(String meterId) {
        ArrayList<ConcentratorCollectorMeterMapVo> vos = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("select id, concentrator_id, collector_id, meter_id, update_date, create_date from concentrator_collector_meter_map where meter_id =? order by update_date desc;");
        try {
//            conn = pool.getConnection();
            conn = DbcpProvider.getDataSource().getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, meterId);
            rs = ps.executeQuery();

            while (rs.next()) {
                ConcentratorCollectorMeterMapVo vo = new ConcentratorCollectorMeterMapVo();
                vo.setId(rs.getString(1));
                vo.setConcentratorId(rs.getString(2));
                vo.setCollectorId(rs.getString(3));
                vo.setMeterId(rs.getString(4));
                vo.setUpdateDate(rs.getString(5));
                vo.setCreateDate(rs.getString(6));
                vos.add(vo);
            }
        } catch (Exception e) {
            log.error(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
            try {
                if (conn != null) {
//                    pool.releaseConnection(conn);
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }

        }

        return vos;
    }

    /**
     * 查询集中器Id对应的表
     *
     * @param concentratorId
     * @return
     */
    public ArrayList<ConcentratorCollectorMeterMapVo> queryMapByConcentratorId(String concentratorId) {
        ArrayList<ConcentratorCollectorMeterMapVo> vos = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("select id, concentrator_id, collector_id, meter_id, update_date, create_date from concentrator_collector_meter_map where concentrator_id=? order by update_date desc;");
        try {
//            conn = pool.getConnection();
            conn = DbcpProvider.getDataSource().getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, concentratorId);
            rs = ps.executeQuery();

            while (rs.next()) {
                ConcentratorCollectorMeterMapVo vo = new ConcentratorCollectorMeterMapVo();
                vo.setId(rs.getString(1));
                vo.setConcentratorId(rs.getString(2));
                vo.setCollectorId(rs.getString(3));
                vo.setMeterId(rs.getString(4));
                vo.setUpdateDate(rs.getString(5));
                vo.setCreateDate(rs.getString(6));
                vos.add(vo);
            }
        } catch (Exception e) {
            log.error(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
            try {
                if (conn != null) {
//                    pool.releaseConnection(conn);
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }

        }

        return vos;
    }


    public int saveMap(ConcentratorCollectorMeterMapVo vo) {
        log.info("ConcentratorCollectorMeterMapVo saveMap(ConcentratorCollectorMeterMapVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        StringBuffer sql = new StringBuffer();
        sql.append("insert into concentrator_collector_meter_map (id, concentrator_id, collector_id, meter_id, update_date," +
                "create_date) values (?,?,?,?,?,?)");
        try {
//            conn = pool.getConnection();
            conn = DbcpProvider.getDataSource().getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, vo.getId());
            ps.setString(2, vo.getConcentratorId());
            ps.setString(3, vo.getCollectorId());
            ps.setString(4, vo.getMeterId());
            ps.setString(5, TimeTag.getStringDate());
            ps.setString(6, TimeTag.getStringDate());

            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("保存出错", e);
            return -1;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
            try {
                if (conn != null) {
//                    pool.releaseConnection(conn);
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }


    /**
     * 更新
     *
     * @param vo
     * @return
     * @throws Exception
     */
    public int updateMap(ConcentratorCollectorMeterMapVo vo) throws Exception {
        log.info("ConcentratorInfoDao updateConcentratorInfo(ConcentratorInfoVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
//            conn = pool.getConnection();
            conn = DbcpProvider.getDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("update concentrator_collector_meter_map set concentrator_id = ?, collector_id=?, update_date=? where meter_id = ?");
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, vo.getConcentratorId());
            ps.setString(2, vo.getCollectorId());
            ps.setString(3, TimeTag.getStringDate());
            ps.setString(4, vo.getMeterId());

            int result = ps.executeUpdate();

            return result;
        } catch (Exception e) {
            log.error("更新出错！", e);
            return -1;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
            try {
                if (conn != null) {
//                    pool.releaseConnection(conn);
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }


    /**
     * 删除
     *
     * @param meterId
     */
    public void deleteMapByMeterId(String meterId) {
        log.info("ConcentratorInfoDao updateConcentratorInfo(ConcentratorInfoVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
//            conn = pool.getConnection();
            conn = DbcpProvider.getDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("delete from concentrator_collector_meter_map where meter_id = ?");
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, meterId);

            ps.execute();
        } catch (Exception e) {
            log.error("s出错！", e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
            try {
                if (conn != null) {
//                    pool.releaseConnection(conn);
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }

    public void deleteMapByConcentratorId(String concentratorId){
        log.info("ConcentratorInfoDao updateConcentratorInfo(ConcentratorInfoVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
//            conn = pool.getConnection();
            conn = DbcpProvider.getDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("delete from concentrator_collector_meter_map where concentrator_id = ?");
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, concentratorId);

            ps.execute();
        } catch (Exception e) {
            log.error("删除出错！", e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
            try {
                if (conn != null) {
//                    pool.releaseConnection(conn);
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }


    public int saveOrUpdateMap(ArrayList<ConcentratorCollectorMeterMapVo> voList) {
        log.info("ConcentratorCollectorMeterMapVo saveMap(ConcentratorCollectorMeterMapVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        StringBuffer saveSql = new StringBuffer();

        try {
//            conn = pool.getConnection();
            conn = DbcpProvider.getDataSource().getConnection();

            saveSql.append("insert into concentrator_collector_meter_map (id, concentrator_id, collector_id, meter_id, update_date," +
                    "create_date) values ");
            for (int i = 0; i < voList.size(); i++) {
                ConcentratorCollectorMeterMapVo vo = voList.get(i);

                saveSql.append(String.format("('%s','%s','%s','%s','%s','%s')", vo.getId(), vo.getConcentratorId(),
                        vo.getCollectorId(), vo.getMeterId(), TimeTag.getStringDate(), TimeTag.getStringDate()));
                if (i < voList.size() - 1) {
                    saveSql.append(",");
                }
            }
            saveSql.append(" ON DUPLICATE KEY UPDATE concentrator_id=VALUES(concentrator_id)," +
                    "collector_id=VALUES(collector_id)," +
                    "update_date=VALUES(update_date)," +
                    "create_date=VALUES(create_date)");

            log.info("sql-----" + saveSql.toString());
            System.out.println(saveSql.toString());
            ps = conn.prepareStatement(saveSql.toString());

            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("保存出错", e);
            return -1;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
            try {
                if (conn != null) {
//                    pool.releaseConnection(conn);
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }

}
