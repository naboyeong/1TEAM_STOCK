import { React, useState, useEffect } from 'react';
import '../styles/MainVars.css';
import '../styles/MainStyle.css';
import { useNavigate } from 'react-router-dom';

const MainPage = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  const [filteredStocks, setFilteredStocks] = useState([]);
  const [stockData, setStockData] = useState({}); // WebSocket에서 받은 실시간 데이터 저장

  // 코스피, 코스닥, 나스닥, S&P 500 종목코드 (MarketIndex에서 map으로 사용)
  const stockIds = ['5566', '3344', '1122', '8899']; // 항상 보여줄 stockId 리스트

  const handleSearch = () => {
    if (searchTerm.trim()) {
      navigate(`/search?query=${searchTerm}`);
    }
  };

  useEffect(() => {
    const fetchStockIds = async () => {
      try {
        const response = await fetch(
          'http://localhost:8080/get-rankings-daily',
          {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
          }
        );
        if (!response.ok) {
          throw new Error('실시간 랭킹 10 ID 검색 실패');
        }

        const stockIdsFromApi = await response.json(); // 주어진 stockId 배열

        // 코스피, 코스닥, 나스닥, S&P 500 추가
        const stockIds = [...stockIdsFromApi, '5566', '3344', '1122', '8899'];

        console.log(JSON.stringify(stockIds));
        // Backend로 subscriptionList 전달
        await fetch('http://localhost:8080/subscriptions/update', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(stockIds),
        });

        const stockDataPromises = stockIds.map(async (stockId) => {
          // 각 stockId에 대한 POST 및 GET 처리
          await fetch(`http://localhost:8080/api/daily-price/${stockId}`, {
            method: 'POST',
          });

          const dailyResponse = await fetch(
            `http://localhost:8080/api/daily-price/${stockId}`
          );
          if (!dailyResponse.ok) {
            throw new Error(`Daily 데이터 검색 실패 for stockId: ${stockId}`);
          }

          const dailyData = await dailyResponse.json();

          // `date` 기준으로 가장 최근 데이터 선택
          const latestData = dailyData.reduce((latest, current) =>
            current.date > (latest?.date || 0) ? current : latest
          );

          return {
            stockId,
            ...latestData, // 가장 최근 데이터만 사용
          };
        });

        const stockData = await Promise.all(stockDataPromises);
        setFilteredStocks(stockData);
      } catch (error) {
        console.error('검색 데이터 로드 실패:', error);
      }
    };

    fetchStockIds();
  }, []);

  // WebSocket 연결
  useEffect(() => {
    const socket = new WebSocket('ws://localhost:8080/ws/stock');

    socket.onmessage = (event) => {
      const data = JSON.parse(event.data);

      // 실시간 데이터 갱신
      setStockData((prevData) => ({
        ...prevData,
        [data.stockId]: data,
      }));
    };

    socket.onopen = () => {
      console.log('WebSocket 연결 성공');
    };

    socket.onerror = (error) => {
      console.error('WebSocket 에러:', error);
    };

    socket.onclose = () => {
      console.log('WebSocket 연결 종료');
    };

    return () => {
      socket.close();
    };
  }, []);

  const fetchRedisFallback = async (stockId) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/redis-data/${stockId}`
      );
      if (!response.ok) {
        throw new Error(`Redis 데이터 검색 실패 for stockId: ${stockId}`);
      }
      const data = await response.json();
      if (data.length > 0) {
        return JSON.parse(data[0]);
      }
      return null;
    } catch (error) {
      console.error(error);
      return null;
    }
  };

  // MarketIndex 컴포넌트
  const MarketIndex = ({ stockId, filteredStocks }) => {
    const stockIdData = filteredStocks.find(
      (stock) => stock.stockId === stockId
    );

    const currentPrice = stockIdData?.currentPrice || 'N/A';
    const fluctuationPrice = stockIdData?.fluctuationPrice || 'N/A';
    const fluctuationRate = stockIdData?.fluctuationRate || 'N/A';

    return (
      <div className="dashboard">
        <div className="frame-5">
          <div className="frame-4">
            <div className="kospi">{stockId}</div>
            <div className="frame-3">
              <div className="_15-550">{currentPrice}</div>
              <div className="div5">
                <div
                  style={{
                    color: fluctuationPrice.includes('-')
                      ? '#2175F2'
                      : '#FF4726',
                  }}
                >
                  {fluctuationPrice}
                </div>
                <div
                  style={{
                    color: fluctuationRate.includes('-')
                      ? '#2175F2'
                      : '#FF4726',
                  }}
                >
                  {fluctuationRate}%
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  };

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
          {/* Market Index Section */}
          <div className="market-index">
            <div className="div3">
              <div className="div4">📊 주가 지수</div>
              <div className="box">
                {stockIds.map((stockId) => (
                  <MarketIndex
                    key={stockId}
                    stockId={stockId}
                    filteredStocks={filteredStocks}
                  />
                ))}
              </div>
            </div>
          </div>

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
                {filteredStocks.map((stock, index) => {
                  const currentData =
                    stockData[stock.stockId]?.currentPrice || 'N/A';
                  const fluctuationPrice =
                    stockData[stock.stockId]?.fluctuationPrice || 'N/A';
                  const fluctuationRate =
                    stockData[stock.stockId]?.fluctuationRate || 'N/A';

                  if (
                    currentData ||
                    fluctuationPrice ||
                    fluctuationRate === 'N/A'
                  ) {
                    fetchRedisFallback(stock.stockId).then((redisData) => {
                      if (redisData) {
                        setStockData((prevData) => ({
                          ...prevData,
                          [stock.stockId]: redisData,
                        }));
                      }
                    });
                  }

                  return (
                    <tr
                      key={stock.stockId}
                      onClick={() => navigate(`/stock/${stock.stockId}`)}
                      style={{ cursor: 'pointer' }}
                    >
                      <td>{index + 1}</td>
                      <td>{stock.stockName}</td>
                      <td>{stock.volume}</td>
                      <td>{currentData}</td>
                      <td
                        style={{
                          color: fluctuationPrice > 0 ? '#FF4726' : '#2175F2',
                        }}
                      >
                        {fluctuationPrice > 0
                          ? `+${fluctuationPrice}`
                          : fluctuationPrice}
                      </td>
                      <td
                        style={{
                          color:
                            parseFloat(
                              stockData[stock.stockId]?.fluctuationRate
                            ) > 0
                              ? '#FF4726'
                              : '#2175F2',
                        }}
                      >
                        {stockData[stock.stockId]?.fluctuationRate}%
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MainPage;
