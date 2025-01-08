import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { StockInfo, TabsContainer, Tab } from '../styles/StockPageStyle';
import '../styles/MainVars.css';
import '../styles/MainStyle.css';

const StockPage = () => {
  const { stockId } = useParams(); // URL에서 stockId 가져오기
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState('realtime');
  const [dailyData, setDailyData] = useState([]); // 일별 데이터 상태
  const [stockData, setStockData] = useState({}); // 모든 종목 데이터
  const [selectedStock, setSelectedStock] = useState(null); // 선택된 종목 데이터
  const navigate = useNavigate();

  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  const handleSearch = () => {
    if (searchTerm.trim()) {
      navigate(`/search?query=${searchTerm}`);
    }
  };
  // Redis에서 초기 데이터 로드
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/redis-data');
        const data = await response.json();
        const parsedData = {};

        // 데이터를 stockId를 키로 하는 형태로 변환
        for (const [key, value] of Object.entries(data)) {
          parsedData[key.replace('stock:', '')] = value.map(JSON.parse); // Redis key에서 "stock:" 제거
        }

        setStockData(parsedData);
        // 선택된 stockId 일치하는 데이터를 설정
        const selected = Object.values(parsedData).find(
          (stock) => stock[0]?.id === stockId
        );
        if (selected) {
          setSelectedStock(selected[0]); // 첫 번째 데이터를 기본으로 설정
        }
      } catch (error) {
        console.error('Redis 초기 데이터 로드 실패:', error);
      }
    };

    fetchInitialData();
  }, [stockId]);

  // WebSocket 연결
  useEffect(() => {
    const socket = new WebSocket('ws://localhost:8080/ws/stock');

    socket.onmessage = (event) => {
      const data = JSON.parse(event.data);
      console.log(data);

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
  }, [stockId]);

  // daily
  useEffect(() => {
    const fetchDailyData = async () => {
      try {
        // 실제 API 호출 코드
        const response = await fetch(
          `http://localhost:8080/api/daily-price/${stockId}`
        );
        const data = await response.json();
        setDailyData(data);

        // // 더미 데이터 사용
        // setDailyData(dummyDailyData);
      } catch (error) {
        console.error('일별 데이터 로드 실패:', error);
      }
    };

    fetchDailyData();
  }, [stockId]);

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
          {selectedStock ? (
            <>
              <div className="stockName">{selectedStock.stockId}</div>
              <div className="current">{selectedStock.currentPrice}원</div>
              <div className="change-section">
                <div className="label">어제보다</div>
                <div className="change">
                  {selectedStock.fluctuationPrice}{' '}
                  {selectedStock.fluctuationSign}(
                  {selectedStock.fluctuationRate})
                </div>
              </div>
            </>
          ) : (
            <>
              <div className="stockName">종목이름</div>
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
                  {selectedStock &&
                    stockData[selectedStock.stockId]?.map((data, index) => (
                      <tr key={index}>
                        <td>{data.currentPrice}</td>
                        <td>{data.transactionVolume}</td>
                        <td
                          style={{
                            color:
                              parseFloat(data.fluctuationPrice) > 0
                                ? '#FF4726'
                                : '#2175F2',
                          }}
                        >
                          {parseFloat(data.fluctuationPrice) > 0
                            ? `${data.fluctuationPrice}`
                            : data.fluctuationPrice}
                        </td>
                        <td
                          style={{
                            color:
                              parseFloat(data.fluctuationRate) > 0
                                ? '#FF4726'
                                : '#2175F2',
                          }}
                        >
                          {data.fluctuationRate}%
                        </td>
                        <td>
                          {data.tradingTime
                            ? `${data.tradingTime.slice(0, 2)}:${data.tradingTime.slice(
                                2,
                                4
                              )}:${data.tradingTime.slice(4)}`
                            : ''}
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
                      <td>{data.stck_bsop_date}</td>
                      <td>{data.stck_clpr}</td>
                      <td
                        style={{
                          color:
                            data.prdy_ctrt > 0
                              ? 'red'
                              : data.prdy_ctrt < 0
                                ? 'blue'
                                : 'black',
                        }}
                      >
                        {data.prdy_ctrt}%
                      </td>
                      <td>{data.acml_vol}</td>
                      <td>{data.stck_oprc}</td>
                      <td>{data.stck_hgpr}</td>
                      <td>{data.stck_lwpr}</td>
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
