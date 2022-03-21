var hasChange = false;
(function($) {
    var myflow = {};


    //js对象深拷贝
    var deepCopy= function(source) {
        var result={};
        for (var key in source) {
            result[key] = typeof source[key]==='object'? deepCopy(source[key]): source[key];
         }
       return result;
    }

    myflow.config = {
        editable: true,
        lineHeight: 15,
        basePath: '',
        ctrlNode:{},
        getCurData:function(){

        },
        rect: { // 状态
            attr: {
                x: 10,
                y: 10,
                width: 100,
                height: 26,
                r: 0,
                fill: '#77bb11',
                "stroke-width": 0
            },
            showType: 'image&text', // image,text,image&text
            type: 'state',
            name: {
                text: 'state',
                'font-style': 'italic'
            },
            text: {
                text: '状态',
                fill: '#ffffff'
            },
            margin: 5,
            props: [],
            img: {}
        },
        path: { // 路径转换
            type: 'path',
            attr: {
                path: {
                    path: 'M10 10L100 100',
                    stroke: '#adabab',
                    fill: "none",
                    "stroke-width": 2
                },
                arrow: {
                    path: 'M10 10L10 10',
                    stroke: '#adabab',
                    fill: "#adabab",
                    "stroke-width": 2,
                    radius: 4
                },
                fromDot: {
                    width: 5,
                    height: 5,
                    stroke: '#fff',
                    fill: '#000',
                    cursor: "move",
                    "stroke-width": 2
                },
                toDot: {
                    width: 5,
                    height: 5,
                    stroke: '#fff',
                    fill: '#000',
                    cursor: "move",
                    "stroke-width": 2
                },
                bigDot: {
                    width: 5,
                    height: 5,
                    stroke: '#fff',
                    fill: '#000',
                    cursor: "move",
                    "stroke-width": 2
                },
                smallDot: {
                    width: 5,
                    height: 5,
                    stroke: '#fff',
                    fill: '#000',
                    cursor: "move",
                    "stroke-width": 3
                },
                text: {
                    cursor: "move",
                    //'background': '#000',
                    fill: '#f75c4c', //改变线上文字的颜色
                }
            },
            text: {
                patten: 'true',
                textPos: {
                    x: 0,
                    y: -10
                }
            },
            props: {
                text: {
                    name: 'text',
                    label: '名称：',
                    value: 'true',
                    editor: function() {
                        return new myflow.editors.inputEditor();
                    }
                },
                pathCdt: {
                    name: 'pathCdt',
                    label: '条件：',
                    value: 'true',
                    editor: function() {
                        return new myflow.editors.inputEditorMore("pathCdt");
                    }
                }
            }
        },
        tools: { // 工具栏
            attr: {
                left: 30,
                top: 30
            },
            pointer: {},
            path: {},
            states: {},
            save: {
                onclick: function(data) {
                	//后台操作--保存到数据库
                	console.log(data);
                	initFlowObj.saveRule(data);
                }
            }
        },
        props: { // 属性编辑器
            attr: {
                top: 30,
                right: 30
            },
            pkgId: "",
            id: "00000",
            props: { text: { value: '新建规则' } }
        },
        restore: ({
            states: {},
            paths: {},
            props: {
                pkgId: "00000",
                id: "00000",
                props: { text: { value: '新建规则' } }
            }
        }),
        activeRects: { // 当前激活状态
            rects: [],
            rectAttr: {
                stroke: '#ff0000',
                "stroke-width": 2
            }
        },
        historyRects: { // 历史激活状态
            rects: [],
            pathAttr: {
                path: {
                    stroke: '#00ff00'
                },
                arrow: {
                    stroke: '#00ff00',
                    fill: "#00ff00"
                }
            }
        }
    };

    myflow.util = {
        isLine: function(p1, p2, p3) { // 三个点是否在一条直线上
            var s, p2y;
            if ((p1.x - p3.x) == 0)
                s = 1;
            else
                s = (p1.y - p3.y) / (p1.x - p3.x);
            p2y = (p2.x - p3.x) * s + p3.y;
            // $('body').append(p2.y+'-'+p2y+'='+(p2.y-p2y)+', ');
            if ((p2.y - p2y) < 10 && (p2.y - p2y) > -10) {
                p2.y = p2y;
                return true;
            }
            return false;
        },
        center: function(p1, p2) { // 两个点的中间点
            return {
                x: (p1.x - p2.x) / 2 + p2.x,
                y: (p1.y - p2.y) / 2 + p2.y
            };
        },
        nextId: (function() {
            var uid = 0;
            return function() {
                return ++uid;
            };
        })(),

        connPoint: function(rect, p) { // 计算矩形中心到p的连线与矩形的交叉点
            var start = p,
                end = {
                    x: rect.x + rect.width / 2,
                    y: rect.y + rect.height / 2
                };
            // 计算正切角度
            var tag = (end.y - start.y) / (end.x - start.x);
            tag = isNaN(tag) ? 0 : tag;

            var rectTag = rect.height / rect.width;
            // 计算箭头位置
            var xFlag = start.y < end.y ? -1 : 1,
                yFlag = start.x < end.x ? -1 : 1,
                arrowTop, arrowLeft;
            // 按角度判断箭头位置
            if (Math.abs(tag) > rectTag && xFlag == -1) { // top边
                arrowTop = end.y - rect.height / 2;
                arrowLeft = end.x + xFlag * rect.height / 2 / tag;
            } else if (Math.abs(tag) > rectTag && xFlag == 1) { // bottom边
                arrowTop = end.y + rect.height / 2;
                arrowLeft = end.x + xFlag * rect.height / 2 / tag;
            } else if (Math.abs(tag) < rectTag && yFlag == -1) { // left边
                arrowTop = end.y + yFlag * rect.width / 2 * tag;
                arrowLeft = end.x - rect.width / 2;
            } else if (Math.abs(tag) < rectTag && yFlag == 1) { // right边
                arrowTop = end.y + rect.width / 2 * tag;
                arrowLeft = end.x + rect.width / 2;
            }
            arrowLeft = isNaN(arrowLeft) ? 0 : arrowLeft;
            arrowTop = isNaN(arrowTop) ? 0 : arrowTop;
            return {
                x: arrowLeft,
                y: arrowTop
            };
        },

        arrow: function(p1, p2, r) { // 画箭头，p1 开始位置,p2 结束位置, r前头的边长
            var atan = Math.atan2(p1.y - p2.y, p2.x - p1.x) * (180 / Math.PI);

            var centerX = p2.x - r * Math.cos(atan * (Math.PI / 180));
            var centerY = p2.y + r * Math.sin(atan * (Math.PI / 180));

            var x2 = centerX + r * Math.cos((atan + 120) * (Math.PI / 180));
            var y2 = centerY - r * Math.sin((atan + 120) * (Math.PI / 180));

            var x3 = centerX + r * Math.cos((atan + 240) * (Math.PI / 180));
            var y3 = centerY - r * Math.sin((atan + 240) * (Math.PI / 180));
            return [p2, {
                x: x2,
                y: y2
            }, {
                x: x3,
                y: y3
            }];
        }
    }

    myflow.rect = function(o, r, id) {
        //新建了长方形，设置是否修改为true
        hasChange = true;
        var _this = this,
            _uid = myflow.util.nextId(),
            _o = $.extend(true, {},
                myflow.config.rect, o),
            _id = 'rect' + _uid,
            _r = r, // Raphael画笔
            _rect, _img, // 图标
            _name, // 状态名称
            _text, // 显示文本
            _ox, _oy; // 拖动时，保存起点位置;
        _o.text.text = id;
        _o.name.text = "";
        var _myflowDom = $("#myflow");

        _rect = _r.rect(parseFloat(_o.attr.x), parseFloat(_o.attr.y), parseFloat(_o.attr.width), parseFloat(_o.attr.height),
            _o.attr.r).hide().attr(_o.attr);

        _img = _r.image(myflow.config.basePath + _o.img.src,
            parseFloat(_o.attr.x) + parseFloat(_o.img.width) / 2,
            parseFloat(_o.attr.y) + (parseFloat(_o.attr.height) - parseFloat(_o.img.height)) / 2, parseFloat(_o.img.width),
            parseFloat(_o.img.height)).hide();
        //svg的长方形节点中的名称
        _name = _r.text(
                parseFloat(_o.attr.x) + parseFloat(_o.img.width) + (parseFloat(_o.attr.width) - parseFloat(_o.img.width)) / 2,
                parseFloat(_o.attr.y) + parseFloat(myflow.config.lineHeight) / 2, _o.name.text).hide()
            .attr(_o.name);
        _text = _r.text(
                parseFloat(_o.attr.x) + parseFloat(_o.img.width) + (parseFloat(_o.attr.width) - parseFloat(_o.img.width)) / 2,
                parseFloat(_o.attr.y) + (parseFloat(_o.attr.height) - parseFloat(myflow.config.lineHeight)) / 2 + parseFloat(myflow.config.lineHeight), _o.text.text).hide()
            .attr(_o.text); // 文本

        // 拖动处理----------------------------------------
        _rect.drag(function(dx, dy) {
            dragMove(dx, dy);
        }, function() {
            dragStart()
        }, function() {
            dragUp();
        });
        _img.drag(function(dx, dy) {
            dragMove(dx, dy);
        }, function() {
            dragStart()
        }, function() {
            dragUp();
        });
        _name.drag(function(dx, dy) {
            dragMove(dx, dy);
        }, function() {
            dragStart()
        }, function() {
            dragUp();
        });
        _text.drag(function(dx, dy) {
            dragMove(dx, dy);
        }, function() {
            dragStart()
        }, function() {
            dragUp();
        });

        var dragMove = function(dx, dy) { // 拖动中
            if (!myflow.config.editable)
                return;

            var x = (_ox + dx); // -((_ox+dx)%10);
            var y = (_oy + dy); // -((_oy+dy)%10);

            _bbox.x = x - _o.margin;
            _bbox.y = y - _o.margin;

            resize();
        };

        var dragStart = function() { // 开始拖动
            _ox = _rect.attr("x");
            _oy = _rect.attr("y");
            _rect.attr({
                opacity: 0.5
            });
            _img.attr({
                opacity: 0.5
            });
            _text.attr({
                opacity: 0.5
            });
        };

        var dragUp = function() { // 拖动结束
            _rect.attr({
                opacity: 1
            });
            _img.attr({
                opacity: 1
            });
            _text.attr({
                opacity: 1
            });
        };

        // 改变大小的边框
        var _bpath, _bdots = {},
            _bw = 5,
            _bbox = {
                x: parseFloat(_o.attr.x) - _o.margin,
                y: parseFloat(_o.attr.y) - _o.margin,
                width: parseFloat(_o.attr.width) + _o.margin * 2,
                height: parseFloat(_o.attr.height) + _o.margin * 2
            };

        _bpath = _r.path('M0 0L1 1').hide();
        _bdots['t'] = _r.rect(0, 0, _bw, _bw).attr({
            fill: '#000',
            stroke: '#fff',
            cursor: 's-resize'
        }).hide().drag(function(dx, dy) {
            bdragMove(dx, dy, 't');
        }, function() {
            bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw / 2, 't');
        }, function() {}); // 上
        _bdots['lt'] = _r.rect(0, 0, _bw, _bw).attr({
            fill: '#000',
            stroke: '#fff',
            cursor: 'nw-resize'
        }).hide().drag(function(dx, dy) {
            bdragMove(dx, dy, 'lt');
        }, function() {
            bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw / 2, 'lt');
        }, function() {}); // 左上
        _bdots['l'] = _r.rect(0, 0, _bw, _bw).attr({
            fill: '#000',
            stroke: '#fff',
            cursor: 'w-resize'
        }).hide().drag(function(dx, dy) {
            bdragMove(dx, dy, 'l');
        }, function() {
            bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw / 2, 'l');
        }, function() {}); // 左
        _bdots['lb'] = _r.rect(0, 0, _bw, _bw).attr({
            fill: '#000',
            stroke: '#fff',
            cursor: 'sw-resize'
        }).hide().drag(function(dx, dy) {
            bdragMove(dx, dy, 'lb');
        }, function() {
            bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw / 2, 'lb');
        }, function() {}); // 左下
        _bdots['b'] = _r.rect(0, 0, _bw, _bw).attr({
            fill: '#000',
            stroke: '#fff',
            cursor: 's-resize'
        }).hide().drag(function(dx, dy) {
            bdragMove(dx, dy, 'b');
        }, function() {
            bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw / 2, 'b');
        }, function() {}); // 下
        _bdots['rb'] = _r.rect(0, 0, _bw, _bw).attr({
            fill: '#000',
            stroke: '#fff',
            cursor: 'se-resize'
        }).hide().drag(function(dx, dy) {
            bdragMove(dx, dy, 'rb');
        }, function() {
            bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw / 2, 'rb');
        }, function() {}); // 右下
        _bdots['r'] = _r.rect(0, 0, _bw, _bw).attr({
            fill: '#000',
            stroke: '#fff',
            cursor: 'w-resize'
        }).hide().drag(function(dx, dy) {
            bdragMove(dx, dy, 'r');
        }, function() {
            bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw / 2, 'r')
        }, function() {}); // 右
        _bdots['rt'] = _r.rect(0, 0, _bw, _bw).attr({
            fill: '#000',
            stroke: '#fff',
            cursor: 'ne-resize'
        }).hide().drag(function(dx, dy) {
            bdragMove(dx, dy, 'rt');
        }, function() {
            bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw / 2, 'rt')
        }, function() {}); // 右上
        $([_bdots['t'].node, _bdots['lt'].node, _bdots['l'].node, _bdots['lb'].node, _bdots['b'].node, _bdots['rb'].node, _bdots['r'].node, _bdots['rt'].node]).click(function() {
            return false;
        });

        var bdragMove = function(dx, dy, t) {
            if (!myflow.config.editable)
                return;
            var x = _bx + dx,
                y = _by + dy;
            var bxHL = _bbox.y - y,
                bxHR = y - _bbox.y,
                bxWL = _bbox.x - x,
                bxWR = x - _bbox.x;
            switch (t) {
                case 't':
                    _bbox.height += bxHL;
                    _bbox.y = y;
                    break;
                case 'lt':
                    _bbox.width += bxWL;
                    _bbox.height += bxHL;
                    _bbox.x = x;
                    _bbox.y = y;
                    break;
                case 'l':
                    _bbox.width += bxWL;
                    _bbox.x = x;
                    break;
                case 'lb':
                    _bbox.height = bxHR;
                    _bbox.width += bxWL;
                    _bbox.x = x;
                    break;
                case 'b':
                    _bbox.height = bxHR;
                    break;
                case 'rb':
                    _bbox.height = bxHR;
                    _bbox.width = bxWR;
                    break;
                case 'r':
                    _bbox.width = bxWR;
                    break;
                case 'rt':
                    _bbox.width = bxWR;
                    _bbox.height += bxHL;
                    _bbox.y = y;
                    break;
            }
            resize();
        };
        var bdragStart = function(ox, oy, t) {
            _bx = ox;
            _by = oy;
        };

        // 事件处理--------------------------------
        $([_rect.node, _text.node, _name.node, _img.node]).bind('click',
            function() {

                if (!myflow.config.editable)
                    return;
                var states = myflow.config.restore.states;
                var paths = myflow.config.restore.paths;
                var legalpath1 = false,
                    legalpath2 = true;
                showBox();
                var mod = $(_r).data('mod');
                switch (mod) {
                    case 'pointer':
                        break;
                    case 'path':
                        var pre = $(_r).data('currNode');
                        var top = $(window).height() / 2 - 155 / 2;
                        var topVal = top + 'px';
                        if (pre && pre.getId() != _id && pre.getId().substring(0, 4) == 'rect') {
                            legalpath1 = true;
                        }
                        //除聚合节点，其余节点入度为1
                        if (legalpath1 && legalpath2 && states[_id].type !== "join") {
                            for (var k in paths) {
                                if (!(paths[k].from == pre.getId() && paths[k].to == _id)) {
                                    if (paths[k].to == _id) {
                                        legalpath2 = false;
                                        new $.zui.Messager('非聚合节点入度只能为一！', {
                                            placement: 'center'
                                        }).show();
                                        break;
                                    }
                                }
                            }
                        }
                        //不能有重复指向的箭头
                        if (legalpath1 && legalpath2) {
                            for (var k in paths) {
                                if (paths[k].from == pre.getId() && paths[k].to == _id) {
                                    legalpath2 = false;
                                    break;
                                }
                            }
                        }
                        //不能有相互指向的箭头
                        if (legalpath1 && legalpath2) {
                            for (var k in paths) {
                                if (paths[k].from == _id && paths[k].to == pre.getId()) {
                                	new $.zui.Messager('两个节点不能有相互指向的箭头!', {
                    				    placement: 'center' // 定义显示位置
                    				}).show();

                                    legalpath2 = false;
                                    break;
                                }
                            }
                        }
                        //开始只能有出去的箭头
                        if (legalpath1 && legalpath2 && states[_id].type == "start") {
                        	new $.zui.Messager('开始节点不能有指向它的流程！', {
            				    placement: 'center' // 定义显示位置
            				}).show();

                            legalpath2 = false;
                        }
                        //结束标签只能有指向它的箭头
                        if (legalpath1 && legalpath2 && states[pre.getId()].type == "end") {
                        	new $.zui.Messager('结束节点不能有指向其他标签的流程!', {
            				    placement: 'center' // 定义显示位置
            				}).show();
                            legalpath2 = false;
                        }


                        //分支只能有一个进入箭头
                        if (legalpath1 && legalpath2 && (states[_id].type == "fork" || states[_id].type == "forkTxt")) {
                            for (var k in paths) {
                                if (paths[k].to == _id) {
                                	new $.zui.Messager('分支节点只能有一个进入流程!', {
                    				    placement: 'center' // 定义显示位置
                    				}).show();

                                    legalpath2 = false;
                                    break;
                                }
                            }
                        }
                        //聚合只能有一个出去的箭头
                        if (legalpath1 && legalpath2 && states[pre.getId()].type == "join") {
                            for (var k in paths) {
                                if (paths[k].from == pre.getId()) {
                                    new $.zui.Messager('聚合节点只能有一个出去的流程!', {
                    				    placement: 'center' // 定义显示位置
                    				}).show();
                                    legalpath2 = false;
                                    break;
                                }
                            }
                        }

                        if (legalpath1 && legalpath2) {
                            $(_r).trigger('addpath', [pre, _this]);
                            $("#myflow").click();
                        }
                        break;
                }
                $(_r).trigger('click', _this);
                if (!(legalpath1 && legalpath2)) {
                    $(_r).data('currNode', _this);
                }
                return false;
            });

        var clickHandler = function(e, src) {
            if (!myflow.config.editable)
                return;
            if (src.getId() == _id) {
                $(_r).trigger('showprops', [_o, src]);
            } else {
                hideBox();
            }
        };
        $(_r).bind('click', clickHandler);

        var textchangeHandler = function(e, text, src) {
            if (src.getId() == _id) {
                _text.attr({
                    text: text
                });
                if(_o != undefined && _o.showType == 'image&text'){
                    var txtLen = text.length*12 + _o.margin * 6 + parseFloat(_o.img.width);
                   _bbox.width = txtLen;
                   resize();
               }else if(_o != undefined && _o.showType == 'text'){
                    var txtLen = text.length*12 + _o.margin * 4;
                   _bbox.width = txtLen;
                   resize();
               }
            }
            hasChange = true;
        };
        $(_r).bind('textchange', textchangeHandler);


        //改变长方形的颜色会调用这个函数
        var colorchangeHandler = function(e, rectColor, txtColor, src) {
            if (src.getId() == _id) {
                if (rectColor != undefined && rectColor != null && rectColor != '') {
                    _rect.attr({
                        fill: rectColor
                    });
                }
                if (txtColor != undefined && txtColor != null && txtColor != '') {
                    _text.attr({
                        fill: txtColor
                    });
                }

            }
        }
        $(_r).bind('colorchange', colorchangeHandler);



        // 私有函数-----------------------
        // 边框路径
        function getBoxPathString() {
            var pathStr = 'M' + _bbox.x + ' ' + _bbox.y + 'L' + _bbox.x + ' ' + (_bbox.y + _bbox.height) + 'L' + (_bbox.x + _bbox.width) + ' ' + (_bbox.y + _bbox.height) + 'L' + (_bbox.x + _bbox.width) + ' ' + _bbox.y + 'L' + _bbox.x + ' ' + _bbox.y;
            return pathStr;
        }
        // 显示边框
        function showBox() {
            _bpath.show();
            for (var k in _bdots) {
                _bdots[k].show();
            }
        }
        // 隐藏
        function hideBox() {
            _bpath.hide();
            for (var k in _bdots) {
                _bdots[k].hide();
            }
        }

        // 根据_bbox，更新位置信息
        function resize() {
            hasChange = true;
            var rx = parseFloat(_bbox.x) + parseFloat(_o.margin),
                ry = parseFloat(_bbox.y) + parseFloat(_o.margin),
                rw = parseFloat(_bbox.width) - parseFloat(_o.margin) * 2,
                rh = parseFloat(_bbox.height) - parseFloat(_o.margin) * 2;
            //让节点的长方形超出svg的范围, 最小宽度和高度是26
            if (rw < 26){ rw = 26;}
            if (rh < 26){ rh = 26;}
            if (rw > 500){ rw = 500;}
            if (rh > 40){ rh = 40;}

            if (rx < _bw){rx = _bw;}
            if (rx + rw + _bw + _o.margin > _myflowDom.width()){rx = _myflowDom.width() - rw - _bw - _o.margin;}
            if (ry < _bw){ry = _bw;}
            if (ry + rh +_bw + _o.margin > _myflowDom.height()){ ry = _myflowDom.height() - rh - _bw - _o.margin;}

            _rect.attr({
                x: rx,
                y: ry,
                width: rw,
                height: rh
            });
            switch (_o.showType) {
                case 'image':
                    _img.attr({
                        x: rx + (rw - _o.img.width) / 2,
                        y: ry + (rh - _o.img.height) / 2
                    }).show();
                    break;
                case 'text':
                    _rect.show();
                    _text.attr({
                        x: rx + rw / 2,
                        y: ry + rh / 2
                    }).show(); // 文本
                    break;
                case 'image&text':
                    _rect.show();
                    _name.attr({
                        x: rx + _o.img.width + (rw - _o.img.width) / 2,
                        y: ry + myflow.config.lineHeight / 2
                    }).show();
                    _text.attr({
                        x: rx + _o.img.width + (rw - _o.img.width) / 2,
                        y: ry + rh / 2
                    }).show(); // 文本
                    _img.attr({
                        x: rx + _o.img.width / 2,
                        y: ry + (rh - _o.img.height) / 2
                    }).show();
                    break;
            }
            //判断长方形缩放外框左侧 x+36 不小于右侧 x 上边界 y+36 不小于下边界 y 
            //长方形缩放外框x, y的值不能小于0， 不能大于画布的范围
            if(_bbox.x < 0) _bbox.x = 0;
            if(_bbox.y < 0) _bbox.y = 0;
            if(_bbox.width < 36){
                _bbox.width = 36;
            }
            if(_bbox.height < 36){
                _bbox.height = 36;
            }
            if (_bbox.width > 510){ _bbox.width = 510;}
            if (_bbox.height > 50){ _bbox.height = 50;}

            if(_bbox.x + _bw + _bbox.width > _myflowDom.width()){
                _bbox.x = _myflowDom.width() - _bw - _bbox.width;
            }
            if(_bbox.y + _bw + _bbox.height > _myflowDom.height()){
                _bbox.y = _myflowDom.height() - _bw - _bbox.height;
            }

            var ltX = _bbox.x - _bw / 2;
            var ltY = _bbox.y - _bw / 2;

            var rbX = _bbox.x - _bw / 2 + _bbox.width;
            var rbY = _bbox.y - _bw / 2 + _bbox.height;


            _bdots['t'].attr({
                x: ltX + _bbox.width / 2,
                y: ltY
            }); // 上
            _bdots['lt'].attr({
                x: ltX,
                y: ltY
            }); // 左上
            _bdots['l'].attr({
                x: ltX,
                y: ltY + _bbox.height / 2
            }); // 左
            _bdots['lb'].attr({
                x: ltX,
                y: rbY
            }); // 左下
            _bdots['b'].attr({
                x: ltX + _bbox.width / 2,
                y: rbY
            }); // 下
            _bdots['rb'].attr({
                x: rbX,
                y: rbY
            }); // 右下
            _bdots['r'].attr({
                x: rbX,
                y: ltY + _bbox.height / 2
            }); // 右
            _bdots['rt'].attr({
                x: rbX,
                y: ltY
            }); // 右上
            _bpath.attr({
                path: getBoxPathString()
            });

            $(_r).trigger('rectresize', _this);
        };

        // 函数----------------
        // 转化json字串
        this.toJson = function() {

        	var json1 = {};
        	var json2 = {};
        	var json3 = {};
        	var json4 = {};
        	json2.text=_text.attr('text');
        	json2.fill=_text.attr('fill');

        	json3.x=Math.round(_rect.attr('x'));
        	json3.y=Math.round(_rect.attr('y'));
        	json3.width=Math.round(_rect.attr('width'));
        	json3.height=Math.round(_rect.attr('height'));
        	json3.fill=_rect.attr('fill');

        	json1.type=_o.type;
        	json1.text=json2;
        	json1.attr=json3;

        	 for (var k in _o.props) {
        		 var json5 = {};
        		 json5.value=_o.props[k].value;
        		 json4[k]=json5;
             }
        	 json1.props=json4;

//            var data = '{"type":"' + _o.type + '","text":{"text":"' + _text.attr('text') + '","fill":"' + _text.attr('fill') + '"}, "attr":{ "x":"' + Math.round(_rect.attr('x')) + '", "y":"' + Math.round(_rect.attr('y')) + '", "width":"' + Math.round(_rect.attr('width')) + '", "height":"' + Math.round(_rect.attr('height')) + '","fill":"' + _rect.attr('fill') + '"}, "props":{';
//            for (var k in _o.props) {
//            	var tmpVal = _o.props[k].value;
//            	if(tmpVal && tmpVal.substring(0,1) == '['){
//            		data += '"' + k + '":{"value":' + tmpVal + '},';
//            	}else{
//            		data += '"' + k + '":{"value":"' + tmpVal + '"},';
//                }
//            }
//            if (data.substring(data.length - 1, data.length) == ',')
//                data = data.substring(0, data.length - 1);
//            data += '}}';
//            return data;
            return JSON.stringify(json1);
        };
        // 从数据中恢复图
        this.restore = function(data) {
            var obj = data;
            // if (typeof data === 'string')
            // obj = eval(data);

            _o = $.extend(true, _o, data);

            _text.attr({
                text: obj.text.text
            });
            resize();
        };

        this.getBBox = function() {
            return _bbox;
        };
        this.getId = function() {
            return _id;
        };
        this.remove = function() {
            _rect.remove();
            _text.remove();
            _name.remove();
            _img.remove();
            _bpath.remove();
            for (var k in _bdots) {
                _bdots[k].remove();
            }
        };
        this.text = function() {
            return _text.attr('text');
        };
        this.attr = function(attr) {
            if (attr)
                _rect.attr(attr);
        };

        resize(); // 初始化位置
    };

    myflow.path = function(o, r, from, to) {
        //新建连线，设置是否修改为true
        hasChange = true;
        var _this = this,
            _r = r,
            _o = $.extend(true, {}, myflow.config.path),
            _path, _arrow, _text, _textPos = _o.text.textPos,
            _ox, _oy, _from = from,
            _to = to,
            _id = 'path' + myflow.util.nextId(),
            _dotList, _autoText = true;
        var _myflowDom = $("#myflow");

        // 点
        function dot(type, pos, left, right) {
            var _this = this,
                _t = type,
                _n, _lt = left,
                _rt = right,
                _ox, _oy, // 缓存移动前时位置
                _pos = pos; // 缓存位置信息{x,y}, 注意：这是计算出中心点

            switch (_t) {
                case 'from':
                    _n = _r.rect(pos.x - _o.attr.fromDot.width / 2,
                            pos.y - _o.attr.fromDot.height / 2,
                            _o.attr.fromDot.width, _o.attr.fromDot.height)
                        .attr(_o.attr.fromDot);
                    break;
                case 'big':
                    _n = _r.rect(pos.x - _o.attr.bigDot.width / 2,
                            pos.y - _o.attr.bigDot.height / 2,
                            _o.attr.bigDot.width, _o.attr.bigDot.height)
                        .attr(_o.attr.bigDot);
                    break;
                case 'small':
                    _n = _r.rect(pos.x - _o.attr.smallDot.width / 2,
                            pos.y - _o.attr.smallDot.height / 2,
                            _o.attr.smallDot.width, _o.attr.smallDot.height)
                        .attr(_o.attr.smallDot);
                    break;
                case 'to':
                    _n = _r.rect(pos.x - _o.attr.toDot.width / 2,
                            pos.y - _o.attr.toDot.height / 2,
                            _o.attr.toDot.width, _o.attr.toDot.height)
                        .attr(_o.attr.toDot);

                    break;
            }
            if (_n && (_t == 'big' || _t == 'small')) {
                _n.drag(function(dx, dy) {
                    dragMove(dx, dy);
                }, function() {
                    dragStart()
                }, function() {
                    dragUp();
                }); // 初始化拖动
                var dragMove = function(dx, dy) { // 拖动中
                    var x = (_ox + dx),
                        y = (_oy + dy);
                    if (x < 0) x = 0;
                    if (y < 0) y = 0;
                    _this.moveTo(x, y);
                };

                var dragStart = function() { // 开始拖动
                    if (_t == 'big') {
                        _ox = _n.attr("x") + _o.attr.bigDot.width / 2;
                        _oy = _n.attr("y") + _o.attr.bigDot.height / 2;
                    }
                    if (_t == 'small') {
                        _ox = _n.attr("x") + _o.attr.smallDot.width / 2;
                        _oy = _n.attr("y") + _o.attr.smallDot.height / 2;
                    }
                };

                var dragUp = function() { // 拖动结束

                };
            }
            $(_n.node).click(function() {
                return false;
            });

            this.type = function(t) {
                if (t)
                    _t = t;
                else
                    return _t;
            };
            this.node = function(n) {
                if (n)
                    _n = n;
                else
                    return _n;
            };
            this.left = function(l) {
                if (l)
                    _lt = l;
                else
                    return _lt;
            };
            this.right = function(r) {
                if (r)
                    _rt = r;
                else
                    return _rt;
            };
            this.remove = function() {
                _lt = null;
                _rt = null;
                _n.remove();
            };
            this.pos = function(pos) {
                if (pos) {
                    _pos = pos;
                    _n.attr({
                        x: parseFloat(_pos.x) - _n.attr('width') / 2,
                        y: parseFloat(_pos.y) - _n.attr('height') / 2
                    });
                    return this;
                } else {
                    return _pos
                }
            };

            this.moveTo = function(x, y) {
                this.pos({
                    x: x,
                    y: y
                });

                switch (_t) {
                    case 'from':
                        if (_rt && _rt.right() && _rt.right().type() == 'to') {
                            _rt.right().pos(myflow.util.connPoint(
                                _to.getBBox(), _pos));
                        }
                        if (_rt && _rt.right()) {
                            _rt
                                .pos(myflow.util.center(_pos, _rt.right()
                                    .pos()));
                        }
                        break;
                    case 'big':

                        if (_rt && _rt.right() && _rt.right().type() == 'to') {
                            _rt.right().pos(myflow.util.connPoint(
                                _to.getBBox(), _pos));
                        }
                        if (_lt && _lt.left() && _lt.left().type() == 'from') {
                            _lt.left().pos(myflow.util.connPoint(_from
                                .getBBox(), _pos));
                        }
                        if (_rt && _rt.right()) {
                            _rt
                                .pos(myflow.util.center(_pos, _rt.right()
                                    .pos()));
                        }
                        if (_lt && _lt.left()) {
                            _lt.pos(myflow.util.center(_pos, _lt.left().pos()));
                        }
                        // 三个大点在一条线上，移除中间的小点
                        var pos = {
                            x: _pos.x,
                            y: _pos.y
                        };
                        if (myflow.util.isLine(_lt.left().pos(), pos, _rt
                                .right().pos())) {
                            _t = 'small';
                            _n.attr(_o.attr.smallDot);
                            this.pos(pos);
                            var lt = _lt;
                            _lt.left().right(_lt.right());
                            _lt = _lt.left();
                            lt.remove();
                            var rt = _rt;
                            _rt.right().left(_rt.left());
                            _rt = _rt.right();
                            rt.remove();
                            // $('body').append('ok.');
                        }
                        break;
                    case 'small': // 移动小点时，转变为大点，增加俩个小点
                        if (_lt && _rt && !myflow.util.isLine(_lt.pos(), {
                                x: _pos.x,
                                y: _pos.y
                            }, _rt.pos())) {

                            _t = 'big';

                            _n.attr(_o.attr.bigDot);
                            var lt = new dot('small', myflow.util.center(_lt
                                    .pos(), _pos), _lt, _lt
                                .right());
                            _lt.right(lt);
                            _lt = lt;

                            var rt = new dot('small', myflow.util.center(_rt
                                    .pos(), _pos), _rt.left(),
                                _rt);
                            _rt.left(rt);
                            _rt = rt;

                        }
                        break;
                    case 'to':
                        if (_lt && _lt.left() && _lt.left().type() == 'from') {
                            _lt.left().pos(myflow.util.connPoint(_from
                                .getBBox(), _pos));
                        }
                        if (_lt && _lt.left()) {
                            _lt.pos(myflow.util.center(_pos, _lt.left().pos()));
                        }
                        break;
                }

                refreshpath();
            };
        }

        function dotList() {
            // if(!_from) throw '没有from节点!';
            var _fromDot, _toDot, _fromBB = _from.getBBox(),
                _toBB = _to
                .getBBox(),
                _fromPos, _toPos;

            _fromPos = myflow.util.connPoint(_fromBB, {
                x: _toBB.x + _toBB.width / 2,
                y: _toBB.y + _toBB.height / 2
            });
            _toPos = myflow.util.connPoint(_toBB, _fromPos);

            _fromDot = new dot('from', _fromPos, null, new dot('small', {
                x: (_fromPos.x + _toPos.x) / 2,
                y: (_fromPos.y + _toPos.y) / 2
            }));
            _fromDot.right().left(_fromDot);
            _toDot = new dot('to', _toPos, _fromDot.right(), null);
            _fromDot.right().right(_toDot);

            // 转换为path格式的字串
            this.toPathString = function() {
                if (!_fromDot)
                    return '';
                var d = _fromDot,
                    p = 'M' + d.pos().x + ' ' + d.pos().y,
                    arr = '';
                if (_fromDot.pos().x < 0){
                    _fromDot.pos().x = 0;
                }
                if (_fromDot.pos().x > _myflowDom.width()){
                    _fromDot.pos().x = _myflowDom.width();
                }
                if (_fromDot.pos().y < 0){
                    _fromDot.pos().y = 0;
                }
                if (_fromDot.pos().y > _myflowDom.height()) {
                    _fromDot.pos().y = _myflowDom.height();
                }



                // 线的路径
                while (d.right()) {
                    d = d.right();
                    p += 'L' + d.pos().x + ' ' + d.pos().y;
                }
                // 箭头路径
                var arrPos = myflow.util.arrow(d.left().pos(), d.pos(),
                    _o.attr.arrow.radius);
                arr = 'M' + arrPos[0].x + ' ' + arrPos[0].y + 'L' + arrPos[1].x + ' ' + arrPos[1].y + 'L' + arrPos[2].x + ' ' + arrPos[2].y + 'z';
                return [p, arr];
            };
            this.toJson = function() {
            	var dotArr = [];
                var d = _fromDot;//data = "[",
                var tmpObj = {};
                while (d) {
                    if (d.type() == 'big'){
                    	tmpObj.x = Math.round(d.pos().x).toString();
                		tmpObj.y = Math.round(d.pos().y).toString();
//                        data += '{"x":"' + Math.round(d.pos().x) + '","y":"' + Math.round(d.pos().y) + '"},';
                		dotArr.push(tmpObj);
                    }
                    d = d.right();
                }
                /*if (data.substring(data.length - 1, data.length) == ',')
                    data = data.substring(0, data.length - 1);
                data += "]";*/

                return dotArr;
            };
            this.restore = function(data) {
                var obj = data,
                    d = _fromDot.right();
                for (var i = 0; i < obj.length; i++) {
                    d.moveTo(parseFloat(obj[i].x), parseFloat(obj[i].y));
                    d.moveTo(parseFloat(obj[i].x), parseFloat(obj[i].y));
                    d = d.right();
                }

                this.hide();
            };

            this.fromDot = function() {
                return _fromDot;
            };
            this.toDot = function() {
                return _toDot;
            };
            this.midDot = function() { // 返回中间点
                var mid = _fromDot.right(),
                    end = _fromDot.right().right();
                while (end.right() && end.right().right()) {
                    end = end.right().right();
                    mid = mid.right();
                }
                return mid;
            };
            this.show = function() {
                var d = _fromDot;
                while (d) {
                    d.node().show();
                    d = d.right();
                }
            };
            this.hide = function() {
                var d = _fromDot;
                while (d) {
                    d.node().hide();
                    d = d.right();
                }
            };
            this.remove = function() {
                var d = _fromDot;
                while (d) {
                    if (d.right()) {
                        d = d.right();
                        d.left().remove();
                    } else {
                        d.remove();
                        d = null;
                    }
                }
            };
        }

        // 初始化操作
        _o = $.extend(true, _o, o);
        _path = _r.path(_o.attr.path.path).attr(_o.attr.path);
        _arrow = _r.path(_o.attr.arrow.path).attr(_o.attr.arrow);

        _dotList = new dotList();
        _dotList.hide();

        var fromObj = JSON.parse(from.toJson());
        if (fromObj.type == 'fork' || fromObj.type == 'forkTxt') {
            _text = _r.text(0, 0, _o.text.text || _o.text.patten).attr(_o.attr.text);
        } else {
            _text = _r.text(0, 0, '').attr(_o.attr.text);
        }

        _text.drag(function(dx, dy) {
            if (!myflow.config.editable)
                return;
            var clcx = _ox + dx;
            var clcy = _oy + dy;
            if (clcx < 0) clcx = 0;
            if (clcy < 0) clcy = 0;

            _text.attr({
                x: clcx,
                y: clcy
            });
        }, function() {
            _ox = _text.attr('x');
            _oy = _text.attr('y');
        }, function() {
            var mid = _dotList.midDot().pos();
            var clcx = parseFloat(_text.attr('x')) - mid.x;
            var clcy = parseFloat(_text.attr('y')) - mid.y;

            _textPos = {
                x: clcx,
                y: clcy
            };
        });

        refreshpath(); // 初始化路径

        // 事件处理--------------------
        $([_path.node, _arrow.node, _text.node]).bind('click', function() {
            if (!myflow.config.editable)
                return;
            $(_r).trigger('click', _this);
            $(_r).data('currNode', _this);
            return false;
        });

        // 处理点击事件，线或矩形
        var clickHandler = function(e, src) {
            if (!myflow.config.editable)
                return;
            if (src && src.getId() == _id) {
                _dotList.show();
                $(_r).trigger('showprops', [_o, _this]);
            } else {
                _dotList.hide();
            }

            var mod = $(_r).data('mod');
            switch (mod) {
                case 'pointer':

                    break;
                case 'path':

                    break;
            }

        };
        $(_r).bind('click', clickHandler);

        // 删除事件处理
        var removerectHandler = function(e, src) {
            if (!myflow.config.editable)
                return;
            if (src && (src.getId() == _from.getId() || src.getId() == _to
                    .getId())) {
                // _this.remove();
                $(_r).trigger('removepath', _this);
            }
        };
        $(_r).bind('removerect', removerectHandler);

        // 矩形移动事件处理
        var rectresizeHandler = function(e, src) {
            if (!myflow.config.editable)
                return;
            if (_from && _from.getId() == src.getId()) {
                var rp;
                if (_dotList.fromDot().right().right().type() == 'to') {
                    rp = {
                        x: _to.getBBox().x + _to.getBBox().width / 2,
                        y: _to.getBBox().y + _to.getBBox().height / 2
                    };
                } else {
                    rp = _dotList.fromDot().right().right().pos();
                }
                var p = myflow.util.connPoint(_from.getBBox(), rp);
                _dotList.fromDot().moveTo(p.x, p.y);
                refreshpath();
            }
            if (_to && _to.getId() == src.getId()) {
                var rp;
                if (_dotList.toDot().left().left().type() == 'from') {
                    rp = {
                        x: _from.getBBox().x + _from.getBBox().width / 2,
                        y: _from.getBBox().y + _from.getBBox().height / 2
                    };
                } else {
                    rp = _dotList.toDot().left().left().pos();
                }
                var p = myflow.util.connPoint(_to.getBBox(), rp);
                _dotList.toDot().moveTo(p.x, p.y);
                refreshpath();
            }
        };
        $(_r).bind('rectresize', rectresizeHandler);

        var textchangeHandler = function(e, v, src) {
            var states = myflow.config.restore.states;
            var paths = myflow.config.restore.paths;
            var hideTxt = true;
            for (var s in states) {
                if (paths[_id] && paths[_id].from == s && (states[s].type == 'fork' || states[s].type == 'forkTxt')) {
                    hideTxt = false;
                }
            }
            var fromObj = JSON.parse(from.toJson());

            if (src.getId() == _id) { // 改变自身文本
                if (fromObj.type == 'fork' || fromObj.type == 'forkTxt') {
                    _text.attr({
                        text: v
                    });
                } else {
                    _text.attr({
                        text: ''
                    });
                }
                _autoText = false;
            }

            if (_autoText) {
                if (_to.getId() == src.getId() || _from.getId() == src.getId()) {
                    if (fromObj.type != 'fork' && fromObj.type != 'forkTxt') {
                        _text.attr({ text: '' });
                    }
                }
            }
            hasChange = true;
        };
        $(_r).bind('textchange', textchangeHandler);


        //改变线上文字的颜色会用到这个函数
        var colorchangeHandler = function(e, rectColor, txtColor, src) {
            if (src.getId() == _id) {
                if (rectColor != undefined && rectColor != null && rectColor != '') {
                    _rect.attr({
                        fill: rectColor
                    });
                }
                if (txtColor != undefined && txtColor != null && txtColor != '') {
                    _text.attr({
                        fill: txtColor
                    });
                }

            }
            hasChange = true;
        }
        $(_r).bind('colorchange', colorchangeHandler);


        // 函数-------------------------------------------------
        this.from = function() {
            return _from;
        };
        this.to = function() {
            return _to;
        };
        // 转化json数据
        this.toJson = function() {
        	var json1 = {};
        	var json2 = {};
        	var json3 = {};
        	var json4 = {};

        	json3.x= Math.round(_textPos.x);
        	json3.y= Math.round(_textPos.y);
        	json2.text=_text.attr('text');
        	json2.textPos=json3;

        	for (var k in _o.props) {
        		var json5 = {};
        		var val = _o.props[k].value;
        		if(typeof(val) == 'string' && val.substring(0,1) == "{"){
        			json5.value= JSON.parse(val);
        		}else{
        			json5.value= val;
        		}
       		 	json4[k]=json5;
            }
        	json1.from=_from.getId();
        	json1.to=_to.getId();
        	json1.dots=_dotList.toJson();
        	json1.text=json2;
        	json1.props=json4;
        	return JSON.stringify(json1);
            /*var data = '{"from":"' + _from.getId() + '","to":"' + _to.getId() + '", "dots":' + _dotList.toJson() + ',"text":{"text":"' + _text.attr('text') + '","textPos":{"x":"' + Math.round(_textPos.x) + '","y":"' + Math.round(_textPos.y) + '"}}, "props":{';
            for (var k in _o.props) {
            	var tempVal = _o.props[k].value;
            	if(tempVal.substring(0,1) == '{'){
            		data += '"' + k + '":{"value":' + _o.props[k].value + '},';
            	}else{
            		data += '"' + k + '":{"value":"' + _o.props[k].value + '"},';
            	}
            }
            if (data.substring(data.length - 1, data.length) == ',')
                data = data.substring(0, data.length - 1);
            data += '}}';
            return data;
            */
        };
        // 恢复
        this.restore = function(data) {
            var obj = data;

            _o = $.extend(true, _o, data);
            if (_text.attr('text') != _o.text.text && _text.attr('text') != '') {
                _text.attr({ text: _o.text.text });
                _autoText = false;
            }

            _dotList.restore(obj.dots);
        };
        // 删除
        this.remove = function() {
            _dotList.remove();
            _path.remove();
            _arrow.remove();
            _text.remove();
            try {
                $(_r).unbind('click', clickHandler);
            } catch (e) {}
            try {
                $(_r).unbind('removerect', removerectHandler);
            } catch (e) {}
            try {
                $(_r).unbind('rectresize', rectresizeHandler);;
            } catch (e) {}
            try {
                $(_r).unbind('textchange', textchangeHandler);
            } catch (e) {}
            try {
                $(_r).unbind('colorchange', colorchangeHandler);
            } catch (e) {}
        };
        // 刷新路径
        function refreshpath() {
            var p = _dotList.toPathString(),
                mid = _dotList.midDot().pos();

            _path.attr({
                path: p[0]
            });
            _arrow.attr({
                path: p[1]
            });
            var txtX = parseFloat(mid.x) + parseFloat(_textPos.x);
            var txtY = parseFloat(mid.y) + parseFloat(_textPos.y);
            if (txtX < 0) txtX = 0;
            if (txtY < 0) txtY = 0;

            _text.attr({
                x: txtX,
                y: txtY
            });


            hasChange = true;
        }

        this.getId = function() {
            return _id;
        };
        this.text = function() {
            return _text.attr('text');
        };
        this.attr = function(attr) {
            if (attr && attr.path)
                _path.attr(attr.path);
            if (attr && attr.arrow)
                _arrow.attr(attr.arrow);
        };
    };

    myflow.props = function(o, r) {
        var _this = this,
            _pdiv = $('#myflow_props').hide().draggable({
                handle: '#myflow_props_handle',
                containment: "#content"
            }).resizable().css(myflow.config.props.attr).bind('click',
                function() {
                    return false;
                }),
            _tb = _pdiv.find('table#props'),
            _r = r,
            _src;

        var showpropsHandler = function(e, O, src) {
            var props = O.props;
            if (_src && _src.getId() == src.getId()) { // 连续点击不刷新
                //return;
            }
            _src = src;
            $(_tb).find('.editor').each(function() {
                var e = $(this).data('editor');
                if (e)
                    e.destroy();
            });

            _tb.empty();

            //如果是连线并且连线的起点不是分支
            var pathObjs = myflow.config.restore.paths;
            var states = myflow.config.restore.states;
            if (_src.getId().indexOf('path') >= 0) {
                var from = pathObjs[_src.getId()].from;
                for (var rect in states) {
                    if (rect == from && (states[rect].type != 'fork' && states[rect].type != 'forkTxt')) {
                        _pdiv.hide();
                        return;
                    }
                }
            }

            _pdiv.show();
            for (var k in props) {
                _tb.append('<tr><th>' + props[k].label + '</th><td><div id="p' + k + '" class="editor"></div></td></tr>');
                if (props[k].editor)
                    props[k].editor().init(O, k, 'p' + k, src, _r);
                // $('body').append(props[i].editor+'a');
            }
        };
        $(_r).bind('showprops', showpropsHandler);

    };

    // 属性编辑器
    myflow.editors = {
        setCfgData: function(obj) {
            //obj中含有data(返回字符串), cgfId(对应的path或者rect的id)
            //设置动作和条件配置子页面会调用这个方法返回配置动作和条件的值

            if (obj.chgIptType == 'action') {
                $("#paction > input").val(obj.data).trigger("change");
            } else if (obj.chgIptType =='pathCdt') {
                $("#ppathCdt > input").val(obj.data).trigger("change");
            }else if(obj.chgIptType =='condition'){
                $("#pcondition > input").val(obj.data).trigger("change");
            }else{
        		for(var k in obj){
        			$('#p'+k +' > input').val(obj[k]).trigger("change");
        		}
            }
        },
        bindPickColor: function(obj, r, src) {
            var _src = src,
                _r = r;
            //obj是rect或者path对象，r是画图上下文。
            //选择颜色时候绑定函数
            var showDom = $("#curColor");
            var colorWrap = $("#colorWrap");
            if (obj.type != 'path' && obj.type != 'fork' && obj.type != 'join') {
                //点击开始、结束、动作的时候
                colorWrap.show();
                showDom.removeAttr('style').attr("style", "background-color:" + obj.attr.fill + ";color:" + obj.text.fill);
            } else if (obj.type == 'fork' || obj.type == 'join') {
                colorWrap.hide();
                return;
            } else if (obj.type == 'path') {
                //点击线的时候
                colorWrap.show();
                showDom.removeAttr('style').attr("style", "background-color:" + obj.attr.text.fill);
            }

            $("#colorBoard > table td").unbind();
            $("#colorBoard > table td").click(function() {

                var styleStr = $(this).attr("style");
                var styleArr = styleStr.split(";");
                var bkColor, txtColor, i = 0,
                    tempArr;
                for (i = 0; i < styleArr.length; i++) {
                    if (styleArr[i].indexOf('background-color') >= 0) {
                        tempArr = styleArr[i].split(':');
                        bkColor = $.trim(tempArr[1]);
                    } else if (styleArr[i].indexOf('color') >= 0) {
                        tempArr = styleArr[i].split(':');
                        txtColor = $.trim(tempArr[1]);
                    }
                }

                showDom.removeAttr('style').attr("style", "background-color:" + bkColor + ";color:" + txtColor);

                //改变对象中的颜色
                if (obj.type != 'path' && obj.type != 'fork' && obj.type != 'join') {
                    //点击开始、结束、动作的时候
                    obj.attr.fill = bkColor;
                    obj.text.fill = txtColor;
                    //改变长方形背景和其中文字的颜色
                    $(_r).trigger('colorchange', [bkColor, txtColor, _src]);
                } else if (obj.type == 'path') {
                    //点击线的时候
                    obj.attr.text.fill = bkColor;
                    //改变线上文字的颜色
                    $(_r).trigger('colorchange', ['', bkColor, _src]);
                }
            });
        },
        textEditor: function() {
            var _props, _k, _div, _src, _r;
            this.init = function(O, k, div, src, r) {
                _props = O.props;
                _k = k;
                _div = div;
                _src = src;
                _r = r;

                $('<input  style="width:100%;" readOnly="true"/>').val(_src.text()).change(
                    function() {
                        props[_k].value = $(this).val();
                        $(_r).trigger('textchange', [$(this).val(), _src]);
                    }).appendTo('#' + _div);

                $('#' + _div).data('editor', this);
                //改变颜色函数绑定
                myflow.editors.bindPickColor(O, r, _src);
                $('#myflow_props,.flowOtherProp').hide();
                $('#propFootDiv').show();
                $('#content').removeClass('minContent');
            };
            this.destroy = function() {
                $('#' + _div + ' input').each(function() {
                    _props[_k].value = $(this).val();
                    $(_r).trigger('textchange', [$(this).val(), _src]);
                });
            };
        },
        inputEditor: function() {
            var _props, _k, _div, _src, _r;
            this.init = function(O, k, div, src, r) {
                _props = O.props;
                _k = k;
                _div = div;
                _src = src;
                _r = r;
                var pathObjs = myflow.config.restore.paths;

                $('<input id="propTxts" style="width:100%;"/>').change(function() {
                    _props[_k].value = $(this).val();
                    if (_src.getId().indexOf('path') >= 0) {
                        pathObjs[_src.getId()].text.text = $(this).val();
                        pathObjs[_src.getId()].props.text.value = $(this).val();
                    }
                    $(_r).trigger('textchange', [$(this).val(), _src]);
                }).appendTo('#' + _div).bind('keyup', function(event) {
                    if (event.keyCode == "13") {
                        //回车执行更新数据
                        $(this).trigger("change");
                    }
                });

                if (_src.getId().indexOf('path') >= 0) {
                    _props[_k].value = pathObjs[_src.getId()].text.text;
                }
                $("#propTxts").val(_props[_k].value);

                $('#' + _div).data('editor', this);
                //改变颜色函数绑定
                myflow.editors.bindPickColor(O, r, _src);
            }
            this.destroy = function() {
                $('#' + _div + ' input').each(function() {
                    _props[_k].value = $(this).val();
                });
            }
        },
        inputEditorRuleSet: function() { // 引用的规则集Id
            var _props, _k, _div, _src, _r;
            this.init = function(O, k, div, src, r) {
                _props = O.props;
                _k = k;
                _div = div;
                _src = src;
                _r = r;

                $('<input id="propRuleSetId" style="width:100%;"/>').change(function() {
                    _props[_k].value = $(this).val();
                }).appendTo('#' + _div);
                $("#propRuleSetId").val(_props[_k].value);

                $('#' + _div).data('editor', this);
                //改变颜色函数绑定
                myflow.editors.bindPickColor(O, r, _src);
            }
            this.destroy = function() {
                $('#' + _div + ' input').each(function() {
                    _props[_k].value = $(this).val();
                });
            }
        },
        inputEditorIsPublic: function() {
            var _props, _k, _div, _src, _r;
            this.init = function(O, k, div, src, r) {
                _props = O.props;
                _k = k;
                _div = div;
                _src = src;
                _r = r;

                $('<input id="propIsPublic" style="width:100%;"/>').change(function() {
                    _props[_k].value = $(this).val();
                }).appendTo('#' + _div);
                $("#propIsPublic").val(_props[_k].value);

                $('#' + _div).data('editor', this);
                //改变颜色函数绑定
                myflow.editors.bindPickColor(O, r, _src);
            }
            this.destroy = function() {
                $('#' + _div + ' input').each(function() {
                    _props[_k].value = $(this).val();
                });
            }
        },
        inputEditorSec: function() {
            var _props, _k, _div, _src, _r;
            this.init = function(O, k, div, src, r) {
                _props = O.props;
                _k = k;
                _div = div;
                _src = src;
                _r = r;
                var pathObjs = myflow.config.restore.paths;

                $('<input id="propTxtsSec" style="width:100%;"/>').change(function() {
                    _props[_k].value = $(this).val();
                }).appendTo('#' + _div).bind('keyup', function(event) {
                    if (event.keyCode == "13") {
                        //回车执行更新数据
                        $(this).trigger("change");
                    }
                });
                $("#propTxtsSec").val(_props[_k].value);

                $('#' + _div).data('editor', this);
            }
            this.destroy = function() {
                $('#' + _div + ' input').each(function() {
                    _props[_k].value = $(this).val();
                });
            }
        },
        inputEditorMore: function(type, fun) {
            var _props, _k, _t, _div, _src, _r;
            function moreClickFun(){
            	//layer打开配置的页面
                var childURL = '', cfgType = '', bigTitle = '', typeTitle = '', jsDomStr = '';
                var layerTop = $(window).height() / 2 - 516 / 2;
                var objToChild = new Object();
                //保存的时候回调父层的哪个方法
                objToChild.funName = '$.myflow.editors.setCfgData';
                objToChild.coreString = _props[_k].value;
                objToChild.cgfId = _src.getId();
                var packageId = $.trim($("#packageId").val());//规则包Id
                if (type == "action") {
                    childURL = webpath+'/rulem/expRuleFrame?packageId='+packageId+'&optType=actionCfg';
                    objToChild.bigTitle = _props.text.value + '动作配置';
                    objToChild.typeTitle = '动作配置';
                    objToChild.chgIptType = 'action';
                } else if (type == "pathCdt" || type == "condition") {
                    childURL = webpath+'/rulem/expRuleFrame?packageId='+packageId+'&optType=cdtCfg';
                    if($("#ruleType").val()=='9'){//模型固化规则，只显示sql函数
                        childURL += '&ruleType='+$("#ruleType").val()
                    }
                    objToChild.bigTitle = _props.text.value + '条件配置';
                    objToChild.typeTitle = '条件配置';
                    objToChild.chgIptType = 'pathCdt';
                }
                if(type == "condition"){
                    objToChild.chgIptType = 'condition';
                }

                cfgActionLayer = layer.open({
                    type: 2,
                    title: false,
                    shadeClose: true,
                    shade: 0.7,
                    closeBtn: 0,
                    offset: layerTop + 'px',
                    area: ['930px', '516px'],
                    content: childURL,
                    success: function(layero, index) {
                        //调用子页面的方法给title赋值
                        var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：
                        iframeWin.ruleCfg.setData(objToChild);
                    }
                });
            }
            this.init = function(O, k, div, src, r) {
                _props = O.props;
                _obj = O,
                    _k = k;
                _div = div;
                _src = src;
                _r = r;
                var _pathWithCdt = true;
                var states = myflow.config.restore.states;
                var pathObjs = myflow.config.restore.paths;

                $('<input class="moreCdt"  readOnly="true"/>').val(_props[_k].value).change(function() {
                    _props[_k].value = $(this).val();
                }).appendTo('#' + _div);

                if (type == "pathCdt") {
                    //判断是从分支节点出来的线
                    var from = pathObjs[_src.getId()].from;

                    for (var rect in states) {
                        if (rect == from && (states[rect].type == 'fork' || states[rect].type == 'forkTxt')) {
                            //线的from端是分支，可以显示配置条件
                            $(".moreCdt").width('128px');
                            _pathWithCdt = true;
                            break;
                        } else {
                            $(".moreCdt").width('100%');
                            _pathWithCdt = false;
                        }
                    }

                }

                if (_pathWithCdt || type == "action" || type == "condition") {
                    //打开配置子页面函数绑定
                    $(".moreCdt").attr("style","margin-right:7px;width:128px");
                    $('<button class="setMove">...</button>').click(function() {
                        if(fun){
                        	fun();
                        }else{
                        	moreClickFun();
                        }
                    }).appendTo('#' + _div);
                }
                $('#' + _div).data('editor', this);

                //改变颜色函数绑定
                myflow.editors.bindPickColor(O, r, _src);
            }
            this.destroy = function() {
                $('#' + _div + ' input').each(function() {
                    _props[_k].value = $(this).val();
                });
            }
        },
        selectEditor: function(arg) {
            var _props, _k, _div, _src, _r;
            this.init = function(O, k, div, src, r) {
                _props = O.props;
                _k = k;
                _div = div;
                _src = src;
                _r = r;

                if (typeof arg === 'string') {
                    var sle = $('<select  style="width:100%;"/>').val(_props[_k].value).change(function() {
                        _props[_k].value = $(this).val();
                    }).appendTo('#' + _div);
                    $.ajax({
                        type: "GET",
                        url: arg,
                        success: function(data) {
                            var opts = eval(data);
                            if (opts && opts.length) {
                                for (var idx = 0; idx < opts.length; idx++) {
                                    sle.append('<option value="' + opts[idx].value + '">' + opts[idx].name + '</option>');
                                }
                                sle.val(_props[_k].value);
                            }
                        }
                    });
                } else {
                    var sle = $('<input  style="width:100%;height:25px;" id="sltType"/>').val(_props[_k].value).change(function() {
                        _props[_k].value = $(this).val();
                    }).appendTo('#' + _div);
                    easyloader.load('combobox',function(){
                        // alert('load combobox success!');
                        sle.css("width", sle.parent().width() + "px");
                        sle.combobox({});
                        sle.combobox({
                            valueField: 'value',
                            textField: 'name',
                            editable: false,
                            data: arg,
                            panelHeight: 'auto',
                            onLoadSuccess: function() {
                                sle.combobox('setValue', _props[_k].value);
                            },
                            onSelect: function(record) {
                                sle.val(record.value);
                                _props[_k].value = record.value;
                            }
                        });
                    });
                }
                $('#' + _div).data('editor', this);

            };
            this.destroy = function() {
                $('#' + _div + ' input').each(function() {
                    _props[_k].value = $(this).val();
                });
            };
        }
    };

    // 初始化流程
    myflow.init = function(c, o) {
    	$.extend(true, myflow.config, o);
        var _w = $(window).width(),
            _h = $(window).height(),
            _svgW = _w * 1.5,
	        _svgH = _h * 1.5;
	        if(myflow.config.svgWidth){
	            _svgW = myflow.config.svgWidth;
	        }
	        if(myflow.config.svgHeight){
	            _svgH = myflow.config.svgHeight;
	        }
	   var  _r = Raphael(c, _svgW, _svgH),
            _states = {},
            _paths = {},
            _props = {};
        var cntV = 1;

        //复制黏贴功能 
        var ctrlDown = false,
            ctrlKey = 17,
            cmdKey = 91,
            vKey = 86,
            cKey = 67;

        $(c).width(_svgW).height(_svgH);
        //只能有一个开始节点
        var hasStartFun = function(){
            var states = myflow.config.restore.states;
            for (var k in states) {
                if(states[k].type == "start"){
                    var top = $(window).height() / 2 - 155 / 2;
                    var topVal = top + 'px';
                    new $.zui.Messager('一个决策树只能有一个开始节点！', {
    				    placement: 'center' // 定义显示位置
    				}).show();
                    return true;
                }
            }
            return false;
        }

        /**
         * 删除： 删除状态时，触发removerect事件，连接在这个状态上当路径监听到这个事件，触发removepath删除自身；
         * 删除路径时，触发removepath事件
         * 复制黏贴部分代码
         */
        $(document).keydown(function(e) {
            if (!myflow.config.editable)
                return;
            if (e.keyCode == 46) {
                if( $('#expRuleModal').hasClass('in') || $('#pageContent').hasClass('hide') ){ //判断是否是在表达式模态框内 / 规则编辑页内 的删除操作
                    return; //非画布上的删除操作都不作处理
                }
                var c = $(_r).data('currNode');
                if (c) {
                    if (c.getId().substring(0, 4) == 'rect') {
                        $(_r).trigger('removerect', c);
                    } else if (c.getId().substring(0, 4) == 'path') {
                        $(_r).trigger('removepath', c);
                    }

                    $(_r).removeData('currNode');
                }
            }

            //ctrl键按下了
            if (e.keyCode == ctrlKey || e.keyCode == cmdKey){
                ctrlDown = true;
            }

            if (ctrlDown && e.keyCode == cKey){
                //复制长方形, 按下了ctrl+c
                //如果是属性窗口的复制黏贴就不处理
                var actId = document.activeElement.id;
                if(actId != '' && $('#' + actId).parents('#myflow_props').length > 0) return;

                var c = $(_r).data('currNode');
                if (c) {
                    if (c.getId().substring(0, 4) == 'rect') {
                        console.log('按下了ctrl+c!');

                        myflow.config.ctrlNode.cNode = JSON.parse(_states[c.getId()].toJson());
                        cntV = 1;
                    }
                }
            }


            if (ctrlDown && e.keyCode == vKey){
                //复制长方形, 按下了ctrl+v
                //如果是属性窗口的复制黏贴就不处理
                var actId = document.activeElement.id;
                if(actId != '' && $('#' + actId).parents('#myflow_props').length > 0) return;

                console.log('按下了ctrl+v!');
                var vNode = myflow.config.ctrlNode.cNode;
                console.log(vNode);

                var hasStart = false;
                if (vNode!=null && vNode.type == "start") {
                    //判断是否已经存在开始了
                    hasStart = hasStartFun();
                }
                if(vNode!=null && !hasStart) {
                    var id = vNode.text.text;
                    var rectId = myflow.config.ctrlNode.newId;
                    $(_r).trigger('addrect', [vNode.type, {
                        attr: {
                            x: parseInt(vNode.attr.x) + 30 * cntV,
                            y: parseInt(vNode.attr.y) + 30 * cntV,
                            width: parseInt(vNode.attr.width),
                            height: parseInt(vNode.attr.height),
                            fill: vNode.attr.fill
                        },
                        props:vNode.props
                    }, id]);

                    myflow.config.restore.states[rectId] = vNode;

                    cntV++;
                    $("#myflow").click();
                }
                return;

            }
        }).keyup(function(e) {
            //ctrl键弹起来了
            if (e.keyCode == ctrlKey || e.keyCode == cmdKey){
                ctrlDown = false;
            }
        });

        //点击id是myflow的div隐藏props框
        $("#myflow").click(function() {
            $(_r).data('currNode', null);
            $(_r).trigger('click', {
                getId: function() {
                    return '00000000';
                }
            });
            // 隐藏属性div
            $("#myflow_props,.flowOtherProp").hide();
            $('#propFootDiv').show();
            $('#content').removeClass('minContent');
        });

        // 删除事件
        var removeHandler = function(e, src) {
            if (!myflow.config.editable)
                return;
            if (src.getId().substring(0, 4) == 'rect') {
                _states[src.getId()] = null;
                src.remove();
                //同步数据
                var deleteR = delete myflow.config.restore.states[src.getId()];

            } else if (src.getId().substring(0, 4) == 'path') {
                _paths[src.getId()] = null;
                src.remove();
                //同步数据
                var deleteR = delete myflow.config.restore.paths[src.getId()];
            }
        };
        $(_r).bind('removepath', removeHandler);
        $(_r).bind('removerect', removeHandler);

        // 添加状态
        $(_r).bind('addrect', function(e, type, o, id) {
            // $('body').append(type+', ');r
            var rect = new myflow.rect($.extend(true, {},
                myflow.config.tools.states[type], o), _r, id)
            _states[rect.getId()] = rect;
            //同步数据
            myflow.config.restore.states[rect.getId()] = JSON.parse(_states[rect.getId()].toJson());

            //for ctrl+v
            myflow.config.ctrlNode.newId = rect.getId();
        });

        // 添加路径
        var addpathHandler = function(e, from, to) {
            var path = new myflow.path({}, _r, from, to);
            _paths[path.getId()] = path;
            //同步数据
            myflow.config.restore.paths[path.getId()] = JSON.parse(_paths[path.getId()].toJson());
            //让线上显示的文字和props.text.value的值相同
            myflow.config.restore.paths[path.getId()].props.text.value = myflow.config.restore.paths[path.getId()].text.text;
        };
        $(_r).bind('addpath', addpathHandler);

        // 模式
        $(_r).data('mod', 'point');
        if (myflow.config.editable) {
            // 工具栏
            /*$("#myflow_tools").draggable({
                handle: '#myflow_tools_handle',
                containment: "#content"
            }).css(myflow.config.tools.attr);*/

            $('#myflow_tools .node').hover(function() {
                $(this).addClass('mover');
            }, function() {
                $(this).removeClass('mover');
            });
            $('#myflow_tools .selectable').click(function() {
                $('.selected').removeClass('selected');
                $(this).addClass('selected');
                $(_r).data('mod', this.id);
                $("#myflow").click();
            });

            $('#myflow_tools .state').each(function() {
                $(this).draggable({
                    cursor: "move",
                    cursorAt: { top: 23, left: 64 },
                    helper: 'clone'
                });
            });

            $(c).droppable({
                accept: '.state',
                drop: function(event, ui) {
                    //判断拖动是否超出工具箱
                    var perfectPos = false;
                    var toolObj = $('#myflow_tools');
                    var maxLeft = toolObj.offset().left;
                    var minLeft = toolObj.offset().left + toolObj.width();
                    var maxHeight = toolObj.offset().top;
                    var minHeight = toolObj.offset().top + toolObj.height();
                    var targetLeft = ui.helper.offset().left;
                    var targetTop = ui.helper.offset().top;
                    if(targetLeft > minLeft || maxLeft > targetLeft+toolObj.width()){
                    	perfectPos = true;
                    }else if(targetTop > minHeight || targetTop + $('#myflow_tools .state').height() < maxHeight){
                    	perfectPos = true;
                    }
                    if(!perfectPos){
                    	return;
                    }

                    var temp = ui.helper.context.innerHTML;
                    var id = temp.substring(temp.indexOf(">") + 1, temp.length);
                    var hasStart = false;
                    if (id == "开始") {
                        //判断是否已经存在开始了
                        hasStart = hasStartFun();
                    }
                    if(!hasStart) {
                        var cx = ui.helper.offset().left - $('#myflow').offset().left + 40;
                        var cy = ui.helper.offset().top - 140 + $('#content')[0].scrollTop + $('.myflowWap')[0].scrollTop;
                        if (cx < 0) cx = 0;
                        if (cy < 0) cy = 0;
                        $(_r).trigger('addrect', [ui.helper.attr('type'), {
                            attr: {
                                x: cx,
                                y: cy
                            }
                        }, id]);
                        $("#myflow").click();
                    }
                }
            });

            myflow.config.getCurData = function(){
            	 _props = myflow.config.restore.props;
                 // _states = myflow.config.restore.states;
                 // _paths = myflow.config.restore.paths;
                 //_props.props.text.value = $(".modRNIpt input").val();

                 var data = '{"states":{';
                 for (var k in _states) {
                     if (_states[k]) {
                         data += '"' + _states[k].getId() + '":' + _states[k].toJson() + ',';
                     }
                 }
                 if (data.substring(data.length - 1, data.length) == ',')
                     data = data.substring(0, data.length - 1);
                 data += '},"paths":{';
                 for (var k in _paths) {
                     if (_paths[k]) {
                         data += '"' + _paths[k].getId() + '":' + _paths[k].toJson() + ',';
                     }
                 }
                 if (data.substring(data.length - 1, data.length) == ',')
                     data = data.substring(0, data.length - 1);

                 //名字id信息
                 data += '},"props":{';

                 data += '"pkgId": "' + _props.pkgId + '","id":"' + _props.id + '","props":{"text":{"value":"' + _props.props.text.value + '"}}';

                 data += '}}';

            	return data;
            };

            //点了保存决策树按钮的弹窗中的保存按钮
            $(".saveAll").click(function() {
            	var data = myflow.config.getCurData();
                myflow.config.tools.save.onclick(data);

            });

            // 属性框
            new myflow.props({}, _r);
        }
        // 恢复
        if (o.restore) {
            // var data = ((typeof o.restore === 'string') ? eval(o.restore) :
            // o.restore);
            var data = o.restore;
            var rmap = {};

            var tmpRectsObj = new Object();
            tmpRectsObj = deepCopy(myflow.config.tools.states);
            var tmpPathsObj = new Object();

            if (data.states) {
                for (var k in data.states) {
                    var rect = new myflow.rect(
                        $.extend(true, {},
                            myflow.config.tools.states[data.states[k].type],
                            data.states[k]), _r);
                    rect.restore(data.states[k]);
                    rmap[k] = rect;
                    _states[rect.getId()] = rect;
                    if (rect.getId() != k) {
                        myflow.config.restore.states[rect.getId()] = myflow.config.restore.states[k];
                        for (var p in myflow.config.restore.paths) {
                            //更新path中的states id
                            if (myflow.config.restore.paths[p].from == k) {
                                myflow.config.restore.paths[p].from = rect.getId();
                            }
                            if (myflow.config.restore.paths[p].to == k) {
                                myflow.config.restore.paths[p].to = rect.getId();
                            }
                        }
                        delete myflow.config.restore.states[k];
                    }

                }
            }
//            if (data.paths) {
//                for (var k in data.paths) {
//                    var p = new myflow.path($.extend(true, {},
//                            myflow.config.tools.path, data.paths[k]),
//                        _r, rmap[data.paths[k].from],
//                        rmap[data.paths[k].to]);
//                    p.restore(data.paths[k]);
//                    _paths[p.getId()] = p;
//                    tmpPathsObj[p.getId()] = data.paths[k];
//                }
//                myflow.config.restore.paths = tmpPathsObj;
//            }

            if (data.paths) {
                for (var k in myflow.config.restore.paths) {
                    var p = new myflow.path($.extend(true, {},
                            myflow.config.tools.path, myflow.config.restore.paths[k]),
                        _r, rmap[data.paths[k].from],
                        rmap[data.paths[k].to]);
                    p.restore(myflow.config.restore.paths[k]);
                    _paths[p.getId()] = p;
                    tmpPathsObj[p.getId()] = myflow.config.restore.paths[k];
                }
                myflow.config.restore.paths = tmpPathsObj;
            }

            if (data.props) {
                $(".treeName").text(data.props.props.text.value);
                _props = $.extend(true, {}, myflow.config.props.props, data.props);
            }
            hasChange = false;
        }
        // 历史状态
        var hr = myflow.config.historyRects,
            ar = myflow.config.activeRects;
        if (hr.rects.length || ar.rects.length) {
            var pmap = {},
                rmap = {};
            for (var pid in _paths) { // 先组织MAP
                if (!rmap[_paths[pid].from().text()]) {
                    rmap[_paths[pid].from().text()] = {
                        rect: _paths[pid].from(),
                        paths: {}
                    };
                }
                rmap[_paths[pid].from().text()].paths[_paths[pid].text()] = _paths[pid];
                if (!rmap[_paths[pid].to().text()]) {
                    rmap[_paths[pid].to().text()] = {
                        rect: _paths[pid].to(),
                        paths: {}
                    };
                }
            }
            for (var i = 0; i < hr.rects.length; i++) {
                if (rmap[hr.rects[i].name]) {
                    rmap[hr.rects[i].name].rect.attr(hr.rectAttr);
                }
                for (var j = 0; j < hr.rects[i].paths.length; j++) {
                    if (rmap[hr.rects[i].name].paths[hr.rects[i].paths[j]]) {
                        rmap[hr.rects[i].name].paths[hr.rects[i].paths[j]]
                            .attr(hr.pathAttr);
                    }
                }
            }
            for (var i = 0; i < ar.rects.length; i++) {
                if (rmap[ar.rects[i].name]) {
                    rmap[ar.rects[i].name].rect.attr(ar.rectAttr);
                }
                for (var j = 0; j < ar.rects[i].paths.length; j++) {
                    if (rmap[ar.rects[i].name].paths[ar.rects[i].paths[j]]) {
                        rmap[ar.rects[i].name].paths[ar.rects[i].paths[j]]
                            .attr(ar.pathAttr);
                    }
                }
            }
        }
    };

    // 添加jquery方法
    $.fn.myflow = function(o) {
        return this.each(function() {
            myflow.init(this, o);
        });
    };

    $.myflow = myflow;
})(jQuery);
