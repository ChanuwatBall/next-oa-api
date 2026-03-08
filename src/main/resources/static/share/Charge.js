(function () {
  const { useEffect, useState } = React;

  window.Charge = function Charge() {
    const API_BASE_URL = `${window.location.origin}/api/payment`;

    const [charges, setCharges] = useState([]);
    const [successMessage, setSuccessMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [loading, setLoading] = useState(true);

    const showAlert = (message, type = "success") => {
      if (type === "success") {
        setSuccessMessage(message);
        setErrorMessage("");
        setTimeout(() => setSuccessMessage(""), 3000);
      } else {
        setErrorMessage(message);
        setSuccessMessage("");
        setTimeout(() => setErrorMessage(""), 3000);
      }
    };

    const loadCharges = async () => {
      try {
        setLoading(true);
        const response = await fetch(`${API_BASE_URL}/charges`);
        if (!response.ok) {
          throw new Error("Failed to load charges");
        }

        const data = await response.json();
        setCharges(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error("Error loading charges:", error);
        showAlert("ไม่สามารถโหลดข้อมูลได้", "error");
      } finally {
        setLoading(false);
      }
    };

    const updateStatus = async (chargeId, status) => {
      try {
        const response = await fetch(
          `${API_BASE_URL}/charges/${chargeId}/status?status=${status}`,
          {
            method: "PUT",
          }
        );

        if (response.ok) {
          showAlert(
            `Charge ${status === "Success" ? "✓ สำเร็จ" : "✗ ไม่สำเร็จ"} แล้ว`,
            "success"
          );
          loadCharges();
        } else {
          showAlert("ไม่สามารถอัปเดตสถานะได้", "error");
        }
      } catch (error) {
        console.error("Error updating status:", error);
        showAlert("เกิดข้อผิดพลาด", "error");
      }
    };

    const clearAllCharges = async () => {
      const confirmed = window.confirm(
        "คุณแน่ใจหรือ? สิ่งนี้จะลบ Charge ทั้งหมดที่อยู่ในหน่วยความจำ"
      );

      if (!confirmed) return;

      try {
        const response = await fetch(`${API_BASE_URL}/charges/clear`, {
          method: "POST",
        });

        if (!response.ok) {
          throw new Error("Failed to clear charges");
        }

        await response.json();
        showAlert("ลบทั้งหมดแล้ว", "success");
        loadCharges();
      } catch (error) {
        console.error("Error clearing charges:", error);
        showAlert("กรุณารีโหลดแอปพลิเคชัน", "error");
      }
    };

    const formatAmount = (satang) => {
      const baht = Number(satang || 0) / 100;
      return `${baht.toFixed(2)} THB`;
    };

    const formatDate = (dateString) => {
      if (!dateString) return "-";

      const date = new Date(dateString);
      return date.toLocaleString("th-TH", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
      });
    };

    const getStatusBadge = (status) => {
      let background = "#fff3cd";
      let color = "#856404";
      let text = "⏳ รอดำเนินการ";

      if (status === "Success") {
        background = "#d4edda";
        color = "#155724";
        text = "✓ สำเร็จ";
      } else if (status === "Failed") {
        background = "#f8d7da";
        color = "#721c24";
        text = "✗ ไม่สำเร็จ";
      }

      return (
        <span
          style={{
            display: "inline-block",
            padding: "5px 10px",
            borderRadius: "20px",
            fontSize: "12px",
            fontWeight: "500",
            background,
            color,
          }}
        >
          {text}
        </span>
      );
    };

    useEffect(() => {
      loadCharges();

      const interval = setInterval(() => {
        loadCharges();
      }, 5000);

      return () => clearInterval(interval);
    }, []);

    const totalCharges = charges.length;
    const pendingCharges = charges.filter((c) => c.status === "Pending").length;
    const successCharges = charges.filter((c) => c.status === "Success").length;
    const failedCharges = charges.filter((c) => c.status === "Failed").length;

    return (
      <div
        style={{
          fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
          background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
          minHeight: "100vh",
          padding: "20px",
          boxSizing: "border-box",
        }}
      >
        <div
          style={{
            maxWidth: "1200px",
            margin: "0 auto",
            background: "white",
            borderRadius: "10px",
            boxShadow: "0 10px 40px rgba(0, 0, 0, 0.2)",
            padding: "30px",
          }}
        >
          <h1
            style={{
              color: "#333",
              marginBottom: "10px",
              textAlign: "center",
            }}
          >
            💳 Charge Management - Test Mode
          </h1>

          <p
            style={{
              textAlign: "center",
              color: "#666",
              marginBottom: "30px",
              fontSize: "14px",
            }}
          >
            จัดการ Charge ทั้งหมดที่สร้างในโหมด Test
          </p>

          {successMessage && (
            <div
              style={{
                padding: "15px",
                borderRadius: "5px",
                marginBottom: "20px",
                background: "#d4edda",
                color: "#155724",
                border: "1px solid #c3e6cb",
              }}
            >
              {successMessage}
            </div>
          )}

          {errorMessage && (
            <div
              style={{
                padding: "15px",
                borderRadius: "5px",
                marginBottom: "20px",
                background: "#f8d7da",
                color: "#721c24",
                border: "1px solid #f5c6cb",
              }}
            >
              {errorMessage}
            </div>
          )}

          <div
            style={{
              display: "flex",
              gap: "10px",
              marginBottom: "30px",
              justifyContent: "center",
              flexWrap: "wrap",
            }}
          >
            <button
              onClick={loadCharges}
              style={{
                padding: "10px 20px",
                border: "none",
                borderRadius: "5px",
                cursor: "pointer",
                fontSize: "14px",
                fontWeight: "500",
                background: "#667eea",
                color: "white",
              }}
            >
              🔄 รีโหลด
            </button>

            <button
              onClick={clearAllCharges}
              style={{
                padding: "10px 20px",
                border: "none",
                borderRadius: "5px",
                cursor: "pointer",
                fontSize: "14px",
                fontWeight: "500",
                background: "#ff6b6b",
                color: "white",
              }}
            >
              🗑️ ลบทั้งหมด
            </button>
          </div>

          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(150px, 1fr))",
              gap: "15px",
              marginBottom: "30px",
            }}
          >
            {[
              { label: "ทั้งหมด", value: totalCharges },
              { label: "รอดำเนินการ", value: pendingCharges },
              { label: "สำเร็จ", value: successCharges },
              { label: "ไม่สำเร็จ", value: failedCharges },
            ].map((item, index) => (
              <div
                key={index}
                style={{
                  background: "#f5f5f5",
                  padding: "15px",
                  borderRadius: "5px",
                  textAlign: "center",
                  borderLeft: "4px solid #667eea",
                }}
              >
                <div
                  style={{
                    fontSize: "24px",
                    fontWeight: "bold",
                    color: "#333",
                  }}
                >
                  {item.value}
                </div>
                <div
                  style={{
                    fontSize: "12px",
                    color: "#666",
                    marginTop: "5px",
                  }}
                >
                  {item.label}
                </div>
              </div>
            ))}
          </div>

          <div style={{ overflowX: "auto" }}>
            {loading ? (
              <div
                style={{
                  textAlign: "center",
                  padding: "40px",
                  color: "#666",
                }}
              >
                <p>⏳ กำลังโหลดข้อมูล...</p>
              </div>
            ) : charges.length === 0 ? (
              <div
                style={{
                  textAlign: "center",
                  padding: "40px",
                  color: "#999",
                }}
              >
                <div style={{ fontSize: "48px", marginBottom: "10px" }}>📭</div>
                <p>ไม่มี Charge ให้แสดง</p>
              </div>
            ) : (
              <table
                style={{
                  width: "100%",
                  borderCollapse: "collapse",
                  background: "white",
                }}
              >
                <thead
                  style={{
                    background: "#f8f9fa",
                    borderBottom: "2px solid #dee2e6",
                  }}
                >
                  <tr>
                    <th style={thStyle}>Charge ID</th>
                    <th style={thStyle}>Source ID</th>
                    <th style={thStyle}>Amount (Satang)</th>
                    <th style={thStyle}>Status</th>
                    <th style={thStyle}>Created</th>
                    <th style={thStyle}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {charges.map((charge) => (
                    <tr key={charge.id} style={{ borderBottom: "1px solid #dee2e6" }}>
                      <td style={tdStyle}>
                        <code>{charge.id}</code>
                      </td>
                      <td style={tdStyle}>{charge.sourceId}</td>
                      <td style={tdStyle}>{formatAmount(charge.amount)}</td>
                      <td style={tdStyle}>{getStatusBadge(charge.status)}</td>
                      <td style={tdStyle}>{formatDate(charge.createdAt)}</td>
                      <td style={tdStyle}>
                        <div
                          style={{
                            display: "flex",
                            gap: "5px",
                            flexWrap: "wrap",
                          }}
                        >
                          {charge.status !== "Success" && (
                            <button
                              onClick={() => updateStatus(charge.id, "Success")}
                              style={{
                                padding: "5px 10px",
                                fontSize: "12px",
                                border: "none",
                                borderRadius: "3px",
                                cursor: "pointer",
                                background: "#28a745",
                                color: "white",
                              }}
                            >
                              ✓ สำเร็จ
                            </button>
                          )}

                          {charge.status !== "Failed" && (
                            <button
                              onClick={() => updateStatus(charge.id, "Failed")}
                              style={{
                                padding: "5px 10px",
                                fontSize: "12px",
                                border: "none",
                                borderRadius: "3px",
                                cursor: "pointer",
                                background: "#dc3545",
                                color: "white",
                              }}
                            >
                              ✗ ไม่สำเร็จ
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    );
  };

  const thStyle = {
    padding: "15px",
    textAlign: "left",
    color: "#333",
    fontWeight: "600",
    fontSize: "14px",
  };

  const tdStyle = {
    padding: "15px",
    fontSize: "14px",
  };
})();