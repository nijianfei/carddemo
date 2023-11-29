--无参函数
function hello()
    print 'hello'
end

--带参函数，字符串
function test(str)
    print('发过来的消息：' .. str)

    return '发过来的消息：' .. str
end

--检查并加工入参（工卡制作）
function check(str)
    local table = {}

    for k, v in pairs(str) do
        initParams(str)
        --print('k:' .. k .. '  v:' .. v)

        --来访者公司
        if (k == 'company') then
            --print('len:' .. utf8len(v))
            if (utf8len(v) > 10) then
                --print(k .. '_sub:' .. utf8sub(v, 1, 9))
                table[k] = utf8sub(v, 1, 9) .. '...'
            else
                table[k] = v
            end
            goto continue
        end

        --来访者姓名
        if (k == 'name') then
            --print('len:' .. utf8len(v))
            if (utf8len(v) > 4) then
                --print(k .. '_sub:' .. utf8sub(v, 1, 3))
                table[k] = utf8sub(v, 1, 3) .. '..'
            else
                table[k] = v
            end
            goto continue
        end

        --被访者姓名
        if (k == 'intervieweesName') then
            --print('len:' .. utf8len(v))
            if (utf8len(v) > 4) then
                --print(k .. '_sub:' .. utf8sub(v, 1, 3))
                table[k] = utf8sub(v, 1, 3) .. '...'
            else
                table[k] = v
            end
            goto continue
        end

        --来访事由
        if (k == 'visitReason') then
            --print('len:' .. utf8len(v))
            if (utf8len(v) > 9) then
                local visitReasonSub = utf8sub(v, 1, 9)
                --print(k .. '_sub:' .. visitReasonSub)
                table[k] = visitReasonSub
                local visitReasonSub1 = utf8sub(v, 1 + 9, utf8len(v) - 9)
                if (utf8len(visitReasonSub1) > 9) then
                    --print('visitReason1' .. '_sub:' .. utf8sub(visitReasonSub1, 1, 7) .. '...')
                    table['visitReason1'] = utf8sub(visitReasonSub1, 1, 8) .. '...'
                else
                    table['visitReason1'] = visitReasonSub1
                end
            else
                table[k] = v
            end
            goto continue
        end
        table[k] = v
        ::continue::
    end
    --被访者工号-姓名
    table['interviewees']=table['intervieweesId'] .. '-' .. table['intervieweesName']
    if (not table['userId'] or string.len(table['userId']) == 0) then
        table['errorMsg'] = 'userId不能为空'
    end
    return table
end

function initParams(param)
    for k, v in pairs(param) do
        if (v) then
            v = ''
        end
    end
end

function chsize(char)
    if not char then
        return 0
    elseif char > 240 then
        return 4
    elseif char > 225 then
        return 3
    elseif char > 192 then
        return 2
    else
        return 1
    end
end

function utf8len(str)
    local len = 0
    local currentIndex = 1
    while currentIndex <= #str do
        local char = string.byte(str, currentIndex)
        currentIndex = currentIndex + chsize(char)
        len = len + 1
    end
    return len
end

function utf8sub(str, startChar, numChars)
    local startIndex = 1
    while startChar > 1 do
        local char = string.byte(str, startIndex)
        startIndex = startIndex + chsize(char)
        startChar = startChar - 1
    end

    local currentIndex = startIndex
    while numChars > 0 and currentIndex <= #str do
        local char = string.byte(str, currentIndex)
        currentIndex = currentIndex + chsize(char)
        numChars = numChars - 1
    end

    return str:sub(startIndex, currentIndex - 1)
end

function chsize(char)
    if not char then
        return 0
    elseif char > 240 then
        return 4
    elseif char > 225 then
        return 3
    elseif char > 192 then
        return 2
    else
        return 1
    end
end

function utf8len(str)
    local len = 0
    local currentIndex = 1
    while currentIndex <= #str do
        local char = string.byte(str, currentIndex)
        currentIndex = currentIndex + chsize(char)
        len = len + 1
    end
    return len
end

function utf8sub(str, startChar, numChars)
    local startIndex = 1
    while startChar > 1 do
        local char = string.byte(str, startIndex)
        startIndex = startIndex + chsize(char)
        startChar = startChar - 1
    end

    local currentIndex = startIndex
    while numChars > 0 and currentIndex <= #str do
        local char = string.byte(str, currentIndex)
        currentIndex = currentIndex + chsize(char)
        numChars = numChars - 1
    end

    return str:sub(startIndex, currentIndex - 1)
end
