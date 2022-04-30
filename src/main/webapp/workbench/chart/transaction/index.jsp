
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";

    /*
    需求:
        根据交易表中的不同阶段的数量进行一个统计，最终形成一个漏斗图（倒三角）

        将统计出来的阶段的数量比较多的，往上面排列
        将统计出来的阶段的数量比较少的，往下面排列

        例如：
        01资质审查：10条
        02需求分析：85条
        03价值分析：3条
        ...

        sql:




     */


%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>Title</title>
    <script src="ECharts/echarts.min.js"></script>

    <script src="jquery/jquery-1.11.1-min.js"></script>

    <script type="text/javascript">
        $(function () {

            //在页面加载完毕后，我们绘制统计图表
            getCharts();

        })

        function getCharts() {
            $.ajax({
                url:"workbench/transaction/getCharts.do",
                dataType:"json",
                type:"get",
                success:function (data) {
                    /*
                        data
                            {"total":100,
                              "dataList":List<Map<String,String>>
                            }
                     */

// 基于准备好的dom，初始化echarts实例
                    var myChart = echarts.init(document.getElementById('main'));
                    option = {
                        title: {
                            text: '交易漏斗图',
                            subtext:"统计交易阶段数据的漏斗图"
                        },
                        tooltip: {
                            trigger: 'item',
                            formatter: '{a} <br/>{b} : {c}%'
                        },
                        legend: {
                            data: ['Show', 'Click', 'Visit', 'Inquiry', 'Order']
                        },
                        series: [
                            {
                                name: 'Funnel',
                                type: 'funnel',
                                left: '10%',
                                top: 60,
                                bottom: 60,
                                width: '80%',
                                min: 0,
                                max: data.total,
                                minSize: '0%',
                                maxSize: '100%',
                                sort: 'descending',
                                gap: 2,
                                label: {
                                    show: true,
                                    position: 'inside'
                                },
                                labelLine: {
                                    length: 10,
                                    lineStyle: {
                                        width: 1,
                                        type: 'solid'
                                    }
                                },
                                itemStyle: {
                                    borderColor: '#fff',
                                    borderWidth: 1
                                },
                                emphasis: {
                                    label: {
                                        fontSize: 20
                                    }
                                },
                                data: data.dataList

                                /*
                                [
                                    { value: 60, name: '01资质审查' },
                                    { value: 40, name: '02需求分析' },
                                    { value: 20, name: '03价值建议' },
                                    { value: 80, name: '06谈判复审' },
                                    { value: 100, name: '07成交' }
                                ]

                                */
                            }
                        ]
                    };

                    myChart.setOption(option);

                }




            })






            // 使用刚指定的配置项和数据显示图表。


        }

    </script>
</head>
<body>

        <!-- 为 ECharts 准备一个定义了宽高的 DOM -->
        <div id="main" style="width: 600px;height:400px;"></div>

</body>
</html>
