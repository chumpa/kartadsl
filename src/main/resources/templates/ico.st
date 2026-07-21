ico(title, content, sourceSystem, header) ::= <<
<!DOCTYPE html>
<html lang="ru"><head>
<meta charset="utf-8" />
<meta name="generator" content="KartaDSL" />
<meta property="description" content="Сгенерированное описание ICo $title; format="html"$ ($sourceSystem$)"/>
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
<link rel="stylesheet" href="ico.css" />
<title>ICo $title; format="html"$ ($sourceSystem$)</title>
</head>
<body>

    <div class="sap-window">
        <!-- Верхняя статус-панель -->
        <div class="top-status-bar">
            <div class="title">
                <span class="icon">⚡</span> Display Integrated Configuration
            </div>
            <div class="status-fields">
                <label>Status <input type="text" value="Active" readonly></label>
                <label>Displayed Language <input type="text" value="English (OL)" readonly></label>
            </div>
        </div>

        <!-- Секция Sender / Receiver -->
        <div class="form-section header-section">
            <div class="section-title">Sender</div>
            <div class="form-grid">
                <div class="label">Communication Party</div>
                <div class="value"><input type="text" value="$header.fromParty$" readonly></div>

                <div class="label">Communication Component</div>
                <div class="value"><input type="text" value="$header.fromService$" readonly></div>

                <div class="label">Interface</div>
                <div class="value"><input type="text" value="$header.ifaceName$" readonly></div>

                <div class="label">Namespace</div>
                <div class="value"><input type="text" value="$header.ifaceNS$" readonly></div>
            </div>

            <div class="section-title" style="margin-top: 10px;">Receiver</div>
            <div class="form-grid">
                <div class="label">Communication Party</div>
                <div class="value"><input type="text" value="$header.toParty$" readonly></div>

                <div class="label">Communication Component</div>
                <div class="value"><input type="text" value="$header.toService$" readonly></div>

                <div class="label">Description</div>
                <div class="value"><input type="text" value="" readonly></div>
            </div>
        </div>

        <!-- Вкладки (Tabs) -->
        <div class="tabs-container">
            <div class="tab">Inbound Processing</div>
            <div class="tab">Receiver</div>
            <div class="tab active">Receiver Interfaces</div>
            <div class="tab">Outbound Processing</div>
            <div class="tab">Assigned Users</div>
            <div class="tab">Advanced Settings</div>
        </div>

        <!-- Содержимое вкладки -->
        <div class="tab-content">
            <div class="content-subtitle">Assigned Receiver Interfaces</div>

            <!-- Первая таблица -->
            <table class="sap-table">
                <thead>
                    <tr>
                        <th colspan="2">Receiver</th>
                        <th colspan="3">Receiver Interface</th>
                    </tr>
                    <tr>
                        <th>Communication Party</th>
                        <th>Communication Component</th>
                        <th>Name</th>
                        <th>Namespace</th>
                        <th>Software Component Version</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><input type="checkbox"> </td>
                        <td>BC_Test</td>
                        <td>XiPatternInterface2</td>
                        <td>http://sap.com/xi/XI/System/Patterns</td>
                        <td>SAP BASIS 7.50</td>
                    </tr>
                    <tr class="selected-row">
                        <td><input type="checkbox" checked> P_TEST</td>
                        <td>BC_Test</td>
                        <td>XiPatternInterface2</td>
                        <td>http://sap.com/xi/XI/System/Patterns</td>
                        <td>SAP BASIS 7.50</td>
                    </tr>
                </tbody>
            </table>

            <!-- Разделитель и заголовок нижней секции -->
            <div class="section-divider"></div>
            <div class="content-subtitle font-small">
                Configuration for Interface XiPatternInterface2 | http://sap.com/xi/XI/System/Patterns | SAP BASIS 7.50
            </div>

            <!-- Нижняя форма конфигурации каналов -->
            <div class="form-grid bottom-form">
                <div class="label required">Communication Channel *</div>
                <div class="value flex-row">
                    <input type="text" value="CC_001RESTReceiver_BE_KasperskyScanEngine" readonly>
                </div>

                <div class="label">Adapter Type</div>
                <div class="value flex-row">
                    <input type="text" value="REST" style="width: 150px;" readonly>
                    <input type="text" value="http://sap.com/xi/XI/System" readonly>
                    <input type="text" value="SAP BASIS 7.50" style="width: 150px;" readonly>
                </div>

                <div class="label">Adapter Engine</div>
                <div class="value"><input type="text" value="Central Adapter Engine" readonly></div>

                <div class="label disabled-text">Software Component Version of Receiver Interface</div>
                <div class="value"><input type="text" class="disabled-input" value="SAP BASIS 7.50" readonly></div>

                <div class="label disabled-text">Virus Scan</div>
                <div class="value"><input type="text" class="disabled-input" value="Use Global Configuration" readonly></div>

                <div class="label">Schema Validation</div>
                <div class="value radio-group">
                    <label><input type="radio" name="validation" checked disabled> No Validation</label>
                    <label><input type="radio" name="validation" disabled> Validation by Adapter</label>
                </div>
            </div>

            <!-- Секция Header Mapping -->
            <div class="fieldset-box">
                <div class="fieldset-title">Header Mapping</div>
                <div class="form-grid padding-left">
                    <div class="label disabled-text"><input type="checkbox" disabled> Sender Communication Party</div>
                    <div class="value"><input type="text" class="disabled-input" readonly></div>

                    <div class="label disabled-text"><input type="checkbox" disabled> Sender Communication Component</div>
                    <div class="value"><input type="text" class="disabled-input" readonly></div>
                </div>
            </div>

            <!-- Секция Principal Propagation -->
            <div class="fieldset-box">
                <div class="fieldset-title">Principal Propagation Properties</div>
                <div class="form-grid padding-left">
                    <div class="label disabled-text"><input type="checkbox" disabled> Propagate Principal</div>
                    <div class="value"></div>
                </div>
            </div>

        </div>
    </div>

$content$
</body>
</html>
>>