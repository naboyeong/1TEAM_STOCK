import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { StockInfo, TabsContainer, Tab } from '../styles/StockPageStyle';
import '../styles/MainVars.css';
import '../styles/MainStyle.css';

const StockPage = () => {
  const { stockId } = useParams(); // URL에서 stockId 가져오기
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState('realtime');
  const [dailyData, setDailyData] = useState([]); // 일별 데이터 상태
  const [stockData, setStockData] = useState({}); // 초기값을 빈 배열로 설정
  const [selectedStock, setSelectedStock] = useState(null); // 선택된 종목 데이터
  const navigate = useNavigate();
  const [isWebSocketConnected, setIsWebSocketConnected] = useState(false);
  const hasLogged = useRef(false); // 최초 실행 여부 추적

  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  const handleSearch = () => {
    if (searchTerm.trim()) {
      navigate(`/search?query=${searchTerm}`);
    }
  };

  const connectWebSocket = () => {
    const socket = new WebSocket(`wss://${process.env.REACT_APP_STOCK_BACKEND_URL}/ws/stock`);

    socket.onopen = () => {
      console.log('[LOG] WebSocket 연결 성공');

      hasLogged.current=false;
      setIsWebSocketConnected(true);

      socket.onmessage = (event) => {
        const data = JSON.parse(event.data);

        setStockData((prevData) => {
          const updatedStockData = {
            ...prevData,
            [data.stockId]: [
              data,
              ...(prevData[data.stockId] || []).slice(0, 10),
            ], // 최신 5개 데이터 유지
          };

          // WebSocket으로 받은 데이터가 현재 선택된 stockId와 일치하면 업데이트
          if (data.stockId === stockId) {
            setSelectedStock(data);
          }

          return updatedStockData;
        });
      };
    };

    socket.onerror = (error) => {
      console.error('[ERROR] WebSocket 에러:', error);
      hasLogged.current=false;
      setIsWebSocketConnected(false);
      socket.close();
    };

    socket.onclose = () => {
      console.log('[LOG,ERROR] WebSocket 연결 종료');
      hasLogged.current=false;
      setIsWebSocketConnected(false);
    };
    return socket;
  };

  const fetchStockData = async (stockId) => {
    try {
      await fetch(`https://${process.env.REACT_APP_STOCK_BACKEND_URL}/api/daily-price/${stockId}`, {
        method: 'POST',
      });

      const dailyResponse = await fetch(
        `https://${process.env.REACT_APP_STOCK_BACKEND_URL}/api/daily-price/${stockId}`
      );
      if (!dailyResponse.ok) {
        throw new Error(`[ERROR] Daily 데이터 검색 실패 for stockId: ${stockId}`);
      }

      const dailyData = await dailyResponse.json();
      console.log("[LOG] /api/daily-price 성공");

      return dailyData;
    } catch (error) {
      console.error(`[ERROR] Error fetching data for stockId ${stockId}:`, error);
      return null;
    }
  };

  useEffect(() => {
    const fetchInitialData = async (stockId) => {
      console.log("[LOG,MONITORING] StockPage Start at "+ new Date().toLocaleTimeString());
      try {
        const response = await fetch(
          `https://${process.env.REACT_APP_STOCK_BACKEND_URL}/api/redis-data/${stockId}`
        );
        const data = await response.json();

        console.log("[LOG] /api/redis-data "+data);

        // 문자열 배열을 객체 배열로 변환
        const parsedData = data.map((item) => JSON.parse(item));
        setStockData((prevData) => ({
          ...prevData,
          [stockId]: parsedData,
        })); // 상태 업데이트

      } catch (error) {
        console.error('[ERROR] Redis 초기 데이터 로드 실패:', error);
      }
    };

    fetchInitialData(stockId);
  }, [stockId]);

  // WebSocket 연결
  useEffect(() => {
    const socket = connectWebSocket();

    return () => {
      socket.close();
    };
  }, [stockId]);

  // daily
  useEffect(() => {
    const fetchDailyData = async () => {
      try {
        const data = await fetchStockData(stockId);

        if (data) {
          setDailyData(data);
        }
      } catch (error) {
        console.error('[ERROR] 일별 데이터 로드 실패:', error);
      }
    };

    fetchDailyData();

  }, [stockId]);

  useEffect(() => {
    if (!hasLogged.current && isWebSocketConnected && stockData[stockId] && dailyData) {
      hasLogged.current = true;
      console.log("[LOG,MONITORING] StockPage End at "+ new Date().toLocaleTimeString());
    }
  }, [isWebSocketConnected, stockData, dailyData, stockId]);

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

        {/* Stock Info */}

        <StockInfo>
          {dailyData && dailyData.length > 0 ? (
            <div className="stockName">{dailyData[0].stockName}</div>
          ) : null}
          {selectedStock ? (
            <>
              <div className="current">{selectedStock.currentPrice}원</div>
              <div className="change-section">
                <div className="label">어제보다</div>
                <div className="change">
                  {selectedStock.fluctuationPrice} (
                  {selectedStock.fluctuationRate}%){' '}
                  {(() => {
                    switch (selectedStock.fluctuationSign) {
                      case '1':
                        return '상한';
                      case '2':
                        return '상승';
                      case '3':
                        return '보합';
                      case '4':
                        return '하한';
                      case '5':
                        return '하락';
                      default:
                        return 'N/A'; // 알 수 없는 값 처리
                    }
                  })()}
                </div>
              </div>
            </>
          ) : stockData[stockId] && stockData[stockId].length > 0 ? (
            <>
              <div className="current">
                {stockData[stockId][0].currentPrice}원
              </div>
              <div className="change-section">
                <div className="label">어제보다</div>
                <div className="change">
                  {stockData[stockId][0].fluctuationPrice} (
                  {stockData[stockId][0].fluctuationRate}%){' '}
                  {(() => {
                    switch (stockData[stockId][0].fluctuationSign) {
                      case '1':
                        return '상한';
                      case '2':
                        return '상승';
                      case '3':
                        return '보합';
                      case '4':
                        return '하한';
                      case '5':
                        return '하락';
                      default:
                        return 'N/A'; // 알 수 없는 값 처리
                    }
                  })()}
                </div>
              </div>
            </>
          ) : (
            <>
              <div className="current">9999원</div>
              <div className="change-section">
                <div className="label">어제보다</div>
                <div className="change">+100</div>
              </div>
            </>
          )}
        </StockInfo>

        <TabsContainer>
          <Tab
            active={activeTab === 'realtime'}
            onClick={() => handleTabClick('realtime')}
          >
            실시간 체결정보
          </Tab>
          <Tab
            active={activeTab === 'daily'}
            onClick={() => handleTabClick('daily')}
          >
            일별 시세조회
          </Tab>
        </TabsContainer>

        {activeTab === 'realtime' && (
          <div className="main-content">
            <div className="stock-ranking">
              <table className="stock-table">
                <thead>
                  <tr>
                    <th>체결가</th>
                    <th>체결량(주)</th>
                    <th>등락</th>
                    <th>등락률</th>
                    <th>체결 시간</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedStock
                    ? // 웹소켓에서 받은 데이터 사용
                      (stockData[stockId] || []).map((data, index) => (
                        <tr key={index}>
                          <td>{data.currentPrice || 'N/A'}</td>
                          <td>{data.transactionVolume || 'N/A'}</td>
                          <td
                            style={{
                              color:
                                parseFloat(data.fluctuationPrice) > 0
                                  ? '#FF4726'
                                  : '#2175F2',
                            }}
                          >
                            {parseFloat(data.fluctuationPrice) > 0
                              ? `+${data.fluctuationPrice}`
                              : data.fluctuationPrice || 'N/A'}
                          </td>
                          <td
                            style={{
                              color:
                                parseFloat(data.fluctuationRate) > 0
                                  ? '#FF4726'
                                  : '#2175F2',
                            }}
                          >
                            {data.fluctuationRate || 'N/A'}%
                          </td>
                          <td>
                            {data.tradingTime
                              ? `${data.tradingTime.slice(0, 2)}:${data.tradingTime.slice(
                                  2,
                                  4
                                )}:${data.tradingTime.slice(4)}`
                              : 'N/A'}
                          </td>
                        </tr>
                      ))
                    : // Redis에서 가져온 초기 데이터 사용
                      (stockData[stockId] || []).map((data, index) => (
                        <tr key={index}>
                          <td>{data.currentPrice || 'N/A'}</td>
                          <td>{data.transactionVolume || 'N/A'}</td>
                          <td
                            style={{
                              color:
                                parseFloat(data.fluctuationPrice) > 0
                                  ? '#FF4726'
                                  : '#2175F2',
                            }}
                          >
                            {data.fluctuationPrice || 'N/A'}
                          </td>
                          <td
                            style={{
                              color:
                                parseFloat(data.fluctuationRate) > 0
                                  ? '#FF4726'
                                  : '#2175F2',
                            }}
                          >
                            {data.fluctuationRate || 'N/A'}%
                          </td>
                          <td>
                            {data.tradingTime
                              ? `${data.tradingTime.slice(0, 2)}:${data.tradingTime.slice(
                                  2,
                                  4
                                )}:${data.tradingTime.slice(4)}`
                              : 'N/A'}
                          </td>
                        </tr>
                      ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {activeTab === 'daily' && (
          <div className="main-content">
            <div
              className="stock-ranking"
              style={{ maxHeight: '400px', overflowY: 'scroll' }}
            >
              <table className="stock-table">
                <thead>
                  <tr>
                    <th>일자</th>
                    <th>종가</th>
                    <th>등락률(%)</th>
                    <th>거래량(주)</th>
                    <th>시가</th>
                    <th>고가</th>
                    <th>저가</th>
                  </tr>
                </thead>
                <tbody>
                  {dailyData.map((data, index) => (
                    <tr key={index}>
                      <td>{data.date}</td>
                      <td>{data.close}</td>
                      <td
                        style={{
                          color:
                            data.changeRate > 0
                              ? 'red'
                              : data.changeRate < 0
                                ? 'blue'
                                : 'black',
                        }}
                      >
                        {data.changeRate}%
                      </td>
                      <td>{data.volume}</td>
                      <td>{data.open}</td>
                      <td>{data.high}</td>
                      <td>{data.low}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default StockPage;
