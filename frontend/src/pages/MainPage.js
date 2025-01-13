import { React, useState, useEffect } from 'react';
import '../styles/MainVars.css';
import '../styles/MainStyle.css';
import { useNavigate } from 'react-router-dom';

const MainPage = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  const [filteredStocks, setFilteredStocks] = useState([]);
  const [stockData, setStockData] = useState({}); // WebSocket에서 받은 실시간 데이터 저장
  const [isWebSocketConnected, setIsWebSocketConnected] = useState(false);

  const handleSearch = () => {
    if (searchTerm.trim()) {
      navigate(`/search?query=${searchTerm}`);
    }
  };

  const fetchRedisFallback = async (stockId) => {
    try {
      const response = await fetch(
        `https://${process.env.REACT_APP_STOCK_BACKEND_URL}/api/redis-data/${stockId}`
      );
      if (!response.ok) {
        throw new Error(`Redis 데이터 검색 실패 for stockId: ${stockId}`);
      }
      const data = await response.json();
      // 데이터가 배열일 경우 처리
      return Array.isArray(data) && data.length > 0
        ? JSON.parse(data[0])
        : null;
    } catch (error) {
      console.error(error);
      return null;
    }
  };

  const fetchPopularData = async (stockId) => {
    try {
      const response = await fetch(
        `https://${process.env.REACT_APP_STOCK_BACKEND_URL}/api/get-popular/${stockId}`
      );
      if (!response.ok) {
        throw new Error(`Popular 데이터 검색 실패 for stockId: ${stockId}`);
      }
      const data = await response.json();
      return data;
    } catch (error) {
      console.error(error);
      return null;
    }
  };

  useEffect(() => {
    const fetchStockIds = async () => {
      try {
        const response = await fetch(
          `https://${process.env.REACT_APP_STOCK_BACKEND_URL}/api/get-10-rankings-stockid`,
          {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
          }
        );
        if (!response.ok) {
          throw new Error('실시간 랭킹 10 ID 검색 실패');
        }

        const stockIdsFromApi = await response.json(); // 주어진 stockId 배열

        const stockIds = [...stockIdsFromApi];

        console.log(JSON.stringify(stockIds));
        // Backend로 subscriptionList 전달
        await fetch(`https://${process.env.REACT_APP_STOCK_BACKEND_URL}/subscriptions/update`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(stockIds),
        });

        const stockDataPromises = stockIds.map(async (stockId) => {
          const popularData = await fetchPopularData(stockId);
          return { stockId, ...popularData };
        });

        const stockDataArray = await Promise.all(stockDataPromises);
        const stockDataMap = stockDataArray.reduce((acc, stock) => {
          acc[stock.stockId] = stock;
          return acc;
        }, {});

        setStockData(stockDataMap);
        setFilteredStocks(stockIds);
      } catch (error) {
        console.error('검색 데이터 로드 실패:', error);
      }
    };

    fetchStockIds();
  }, []);

  // WebSocket 연결
  useEffect(() => {
    const socket = new WebSocket(`wss://${process.env.REACT_APP_STOCK_BACKEND_URL}/ws/stock`);

    socket.onmessage = (event) => {
      const data = JSON.parse(event.data);

      // 실시간 데이터 갱신
      setStockData((prevData) => ({
        ...prevData,
        [data.stockId]: { ...prevData[data.stockId], ...data },
      }));
    };

    socket.onopen = () => {
      console.log('WebSocket 연결 성공');
      setIsWebSocketConnected(true);
    };

    socket.onerror = (error) => {
      console.error('WebSocket 에러:', error);
      setIsWebSocketConnected(false);
    };

    socket.onclose = () => {
      console.log('WebSocket 연결 종료');
      setIsWebSocketConnected(false);
    };

    return () => {
      socket.close();
    };
  }, []);

  useEffect(() => {
    filteredStocks.forEach((stockId) => {
      if (!isWebSocketConnected && !stockData[stockId]?.currentPrice) {
        fetchRedisFallback(stockId).then((redisData) => {
          if (redisData) {
            setStockData((prevData) => ({
              ...prevData,
              // 합치기 (기존 데이터 + Redis 데이터)
              [stockId]: { ...prevData[stockId], ...redisData },
            }));
          }
        });
      }
    });
  }, [filteredStocks, stockData, isWebSocketConnected]);

  console.log(stockData);

  return (
    <div className="_0-1-home">
      <div className="frame-45">
        {/* Header */}
        <div className="gnb">
          <div className="frame-11">
            <div className="frame-26">
              <img className="image-6" src="/image-60.png" alt="Logo" />
              <div className="frame-10">
                <div className="frame-9">
                  <div
                    className="gnb-button"
                    onClick={() => navigate('/')}
                    style={{ cursor: 'pointer' }}
                  >
                    홈으로
                  </div>
                  <div
                    className="gnb-button"
                    onClick={() => navigate('/login')}
                    style={{ cursor: 'pointer' }}
                  >
                    로그인
                  </div>
                </div>
              </div>
            </div>
            <div className="search-bar">
              <div className="frame-1">
                <img
                  className="search-01"
                  src="/search-010.svg"
                  alt="Search Icon"
                  style={{ cursor: 'pointer' }}
                  onClick={handleSearch}
                />
                <input
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  placeholder="검색하실 종목 이름을 입력해 주세요."
                  className="div2"
                />
              </div>
            </div>
          </div>
        </div>
        <img className="image-9" src="/image-90.png" alt="Main Graphic" />
        {/* Main Content */}
        <div className="main-content">
          {/* Stock Ranking Section */}
          <div className="stock-ranking">
            <div className="top-10">🔥 인기 급상승 종목 Top 10</div>
            <table className="stock-table">
              <thead>
                <tr>
                  <th>순위</th>
                  <th>종목</th>
                  <th>거래량</th>
                  <th>현재가</th>
                  <th>등락</th>
                  <th>등락률</th>
                </tr>
              </thead>
              <tbody>
                {filteredStocks.length > 0 ? (
                  filteredStocks
                    .map((stockId) => stockData[stockId])
                    .sort((a, b) => a.ranking - b.ranking)
                    .map((stock) => {
                      if (!stock) return null;

                      const currentData = stock.currentPrice || null;
                      const fluctuationPrice = stock.fluctuationPrice || null;
                      const fluctuationRate = stock.fluctuationRate || null;
                      console.log(stock);

                      return (
                        <tr
                          key={stock.stockId}
                          onClick={() => navigate(`/stock/${stock.stockId}`)}
                          style={{ cursor: 'pointer' }}
                        >
                          <td>{stock.ranking}</td>
                          <td>{stock.stockName}</td>
                          <td>{stock.acmlvol}</td>
                          <td>{currentData}</td>
                          <td
                            style={{
                              color:
                                fluctuationPrice > 0 ? '#FF4726' : '#2175F2',
                            }}
                          >
                            {fluctuationPrice}
                          </td>
                          <td
                            style={{
                              color:
                                fluctuationRate > 0 ? '#FF4726' : '#2175F2',
                            }}
                          >
                            {fluctuationRate}%
                          </td>
                        </tr>
                      );
                    })
                ) : (
                  <tr>
                    <td colSpan="6">데이터를 불러오는 중입니다...</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MainPage;
